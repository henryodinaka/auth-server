package ng.min.authserve.config;

public enum EncryptionHeader {

    AUTHORIZATION("Authorization"),
    API_KEY("ClientId"),
    CLIENT_ID("ApiKey");

    String name;

    EncryptionHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
