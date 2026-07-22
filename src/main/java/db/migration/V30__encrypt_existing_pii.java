package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Migración Java que cifra datos PII existentes en la BD.
 *
 * Usa JDBC puro (no JPA) para evitar doble cifrado via AttributeConverter.
 * Streaming de ResultSet para no saturar memoria con tablas grandes.
 * Batch de 100 registros para mejor performance.
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
        String selectSql = "SELECT id, name, paternal_surname, maternal_surname, fcm_token FROM users ORDER BY id";
        String updateSql = "UPDATE users SET name = ?, paternal_surname = ?, maternal_surname = ?, fcm_token = ? WHERE id = ?";

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setFetchSize(Integer.MIN_VALUE);
            ResultSet rs = stmt.executeQuery(selectSql);

            int count = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                while (rs.next()) {
                    String userId = rs.getString("id");  // UUID como String
                    String name = rs.getString("name");
                    String paternalSurname = rs.getString("paternal_surname");
                    String maternalSurname = rs.getString("maternal_surname");
                    String fcmToken = rs.getString("fcm_token");

                    String encryptedName = name != null ? PiiEncryptionHelper.encrypt(name) : null;
                    String encryptedPaternal = paternalSurname != null ? PiiEncryptionHelper.encrypt(paternalSurname) : null;
                    String encryptedMaternal = maternalSurname != null ? PiiEncryptionHelper.encrypt(maternalSurname) : null;
                    String encryptedFcm = fcmToken != null ? PiiEncryptionHelper.encrypt(fcmToken) : null;

                    pstmt.setString(1, encryptedName);
                    pstmt.setString(2, encryptedPaternal);
                    pstmt.setString(3, encryptedMaternal);
                    pstmt.setString(4, encryptedFcm);
                    pstmt.setString(5, userId);  // UUID como String
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
    }

    private void encryptLessorsTable(Connection conn) throws SQLException {
        String selectSql = "SELECT id, phone_number FROM lessors ORDER BY id";
        String updateSql = "UPDATE lessors SET phone_number = ? WHERE id = ?";

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setFetchSize(Integer.MIN_VALUE);
            ResultSet rs = stmt.executeQuery(selectSql);

            int count = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                while (rs.next()) {
                    String lessorId = rs.getString("id");  // UUID como String
                    String phoneNumber = rs.getString("phone_number");

                    String encryptedPhone = phoneNumber != null ? PiiEncryptionHelper.encrypt(phoneNumber) : null;

                    pstmt.setString(1, encryptedPhone);
                    pstmt.setString(2, lessorId);  // UUID como String
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
    }
}
