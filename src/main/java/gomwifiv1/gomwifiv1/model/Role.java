package gomwifiv1.gomwifiv1.model;

public enum Role {
    ADMIN,
    CLIENT_MANAGER,
    USER;

    public String getValue() {
        return this.name();
    }
}
