package aleosh.online.vivia.features.users.lessor.domain.valueobjects;

public class PasskeyCredential {

    private final String credentialId;
    private final String publicKey;
    private final long signCount;

    public PasskeyCredential(String credentialId, String publicKey, long signCount) {
        this.credentialId = credentialId;
        this.publicKey = publicKey;
        this.signCount = signCount;
    }

    // Identificador único generado por el dispositivo al registrar la huella
    public String getCredentialId() { return credentialId; }

    // La llave pública que tu backend usará para verificar que la huella fue correcta
    public String getPublicKey() { return publicKey; }

    // Medida de seguridad de WebAuthn para detectar si la llave fue clonada
    public long getSignCount() { return signCount; }
}
