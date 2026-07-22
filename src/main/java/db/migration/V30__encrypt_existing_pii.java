package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Migración Java que cifra datos PII existentes en la BD.
 *
 * Usa JDBC puro (no JPA) para evitar doble cifrado via AttributeConverter.
 *
 * Enfoque a dos fases por tabla: primero se leen todos los registros a memoria
 * (cerrando el ResultSet), después se actualizan en lotes. Esto evita dos
 * problemas de PostgreSQL:
 *   1. setFetchSize(Integer.MIN_VALUE) es un idiom de MySQL; PostgreSQL lo
 *      rechaza con SQLState 22023 (invalid_parameter_value).
 *   2. Intercalar la lectura de un cursor server-side con UPDATEs sobre la
 *      misma conexión es frágil en PostgreSQL.
 *
 * Las tablas users/lessors son de volumen modesto, así que materializar en
 * memoria es seguro.
 *
 * Nota: Obtiene EncryptionService via PiiEncryptionHelper que fue inicializado
 * por Spring en tiempo de arranque.
 */
public class V30__encrypt_existing_pii extends BaseJavaMigration {
    private static final Logger log = LoggerFactory.getLogger(V30__encrypt_existing_pii.class);
    private static final int BATCH_SIZE = 100;

    @Override
    public void migrate(Context context) throws Exception {
        log.info("=== Iniciando cifrado de PII existentes ===");

        Connection conn = context.getConnection();
        conn.setAutoCommit(false);

        try {
            log.info("Fase 1: Cifrando datos de tabla users...");
            encryptUsersTable(conn);

            log.info("Fase 2: Cifrando datos de tabla lessors...");
            encryptLessorsTable(conn);

            conn.commit();
            log.info("=== Cifrado completado exitosamente ===");
        } catch (Exception e) {
            log.error("ERROR CRÍTICO durante cifrado de PII. Rollback automático.", e);
            conn.rollback();
            throw e;
        }
    }

    private void encryptUsersTable(Connection conn) throws SQLException {
        // Fase A: leer todos los registros a memoria.
        // Cada fila: [id, name, paternal_surname, maternal_surname, fcm_token]
        List<String[]> rows = new ArrayList<>();
        String selectSql = "SELECT id, name, paternal_surname, maternal_surname, fcm_token FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("paternal_surname"),
                        rs.getString("maternal_surname"),
                        rs.getString("fcm_token")
                });
            }
        }

        // Fase B: cifrar y actualizar en lotes.
        String updateSql = "UPDATE users SET name = ?, paternal_surname = ?, maternal_surname = ?, "
                + "fcm_token = ? WHERE id = ?";
        int count = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            for (String[] row : rows) {
                String id = row[0];
                pstmt.setString(1, encryptNullable(row[1]));
                pstmt.setString(2, encryptNullable(row[2]));
                pstmt.setString(3, encryptNullable(row[3]));
                pstmt.setString(4, encryptNullable(row[4]));
                pstmt.setString(5, id);
                pstmt.addBatch();

                count++;
                if (count % BATCH_SIZE == 0) {
                    pstmt.executeBatch();
                    log.info("Procesados {} registros de users", count);
                }
            }
            pstmt.executeBatch();
            log.info("Cifrado completado: {} registros de users cifrados", count);
        }
    }

    private void encryptLessorsTable(Connection conn) throws SQLException {
        // La PK de lessors es user_id (no id).
        // Cada fila: [user_id, phone_number]
        List<String[]> rows = new ArrayList<>();
        String selectSql = "SELECT user_id, phone_number FROM lessors";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("user_id"),
                        rs.getString("phone_number")
                });
            }
        }

        String updateSql = "UPDATE lessors SET phone_number = ? WHERE user_id = ?";
        int count = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            for (String[] row : rows) {
                pstmt.setString(1, encryptNullable(row[1]));
                pstmt.setString(2, row[0]);
                pstmt.addBatch();

                count++;
                if (count % BATCH_SIZE == 0) {
                    pstmt.executeBatch();
                    log.info("Procesados {} registros de lessors", count);
                }
            }
            pstmt.executeBatch();
            log.info("Cifrado completado: {} registros de lessors cifrados", count);
        }
    }

    private String encryptNullable(String value) {
        return value != null ? PiiEncryptionHelper.encrypt(value) : null;
    }
}
