package com.example.safeu2.database;

import java.io.Serializable;
import java.util.UUID;

public class VaultEntry implements Serializable {
    private String id;
    private String accountName;
    private String emailOrId;
    private String password;
    private String type;

    public VaultEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public VaultEntry(String accountName, String emailOrId, String password, String type) {
        this.id = UUID.randomUUID().toString();
        this.accountName = accountName;
        this.emailOrId = emailOrId;
        this.password = password;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getEmailOrId() { return emailOrId; }
    public void setEmailOrId(String emailOrId) { this.emailOrId = emailOrId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
