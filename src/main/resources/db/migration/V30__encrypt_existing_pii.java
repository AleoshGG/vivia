package db.migration;

import aleosh.online.vivia.core.security.encryption.EncryptionService;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Migración Java que cifra datos PII existentes en la BD.
 *
 * Usa JDBC puro (no JPA) para evitar doble cifrado via AttributeConverter.
 * Streaming de ResultSet para no saturar memoria con tablas grandes.
 * Batch de 100 registros para mejor performance.
 */
@Component
public class V30__encrypt_existing_pii extends BaseJavaMigration {
    private static final Logger log = LoggerFactory.getLogger(V30__encrypt_existing_pii.class);
    private static final int BATCH_SIZE = 100;

    private EncryptionService encryptionService;
    private DataSource dataSource;

    @Autowired
    public V30__encrypt_existing_pii(EncryptionService encryptionService, DataSource dataSource) {
        this.encryptionService = encryptionService;
        this.dataSource = dataSource;
    }

    @Override
    public void migrate(Context context) throws Exception {
        log.info("=== Iniciando cifrado de PII existentes ===");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            log.info("Fase 1: Cifrando datos de tabla users...");
            encryptUsersTable(conn);

            log.info("Fase 2: Cifrando datos de tabla lessors...");
            encryptLessorsTable(conn);

            conn.commit();
            log.info("=== Cifrado completado exitosamente ===");
        } catch (Exception e) {
            log.error("ERROR CRÍTICO durante cifrado de PII. Rollback automático.", e);
            throw e;  // Flyway retira el registro de V30 y permite reintentar con backup restaurado
        }
    }

    private void encryptUsersTable(Connection conn) throws SQLException {
        String selectSql = "SELECT id, name, paternal_surname, maternal_surname, fcm_token FROM users ORDER BY id";
        String updateSql = "UPDATE users SET name = ?, paternal_surname = ?, maternal_surname = ?, fcm_token = ? WHERE id = ?";

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setFetchSize(Integer.MIN_VALUE);  // Streaming
            ResultSet rs = stmt.executeQuery(selectSql);

            int count = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                while (rs.next()) {
                    Object userId = rs.getObject("id");
                    String name = rs.getString("name");
                    String paternalSurname = rs.getString("paternal_surname");
                    String maternalSurname = rs.getString("maternal_surname");
                    String fcmToken = rs.getString("fcm_token");

                    String encryptedName = name != null ? encryptionService.encrypt(name) : null;
                    String encryptedPaternal = paternalSurname != null ? encryptionService.encrypt(paternalSurname) : null;
                    String encryptedMaternal = maternalSurname != null ? encryptionService.encrypt(maternalSurname) : null;
                    String encryptedFcm = fcmToken != null ? encryptionService.encrypt(fcmToken) : null;

                    pstmt.setString(1, encryptedName);
                    pstmt.setString(2, encryptedPaternal);
                    pstmt.setString(3, encryptedMaternal);
                    pstmt.setString(4, encryptedFcm);
                    pstmt.setObject(5, userId);
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
                    Object lessorId = rs.getObject("id");
                    String phoneNumber = rs.getString("phone_number");

                    String encryptedPhone = phoneNumber != null ? encryptionService.encrypt(phoneNumber) : null;

                    pstmt.setString(1, encryptedPhone);
                    pstmt.setObject(2, lessorId);
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
