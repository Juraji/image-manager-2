package nl.juraji.imagemanager.model.domain.settings;

import nl.juraji.imagemanager.model.domain.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Juraji on 25-12-2018.
 * Image Manager 2
 */
@Entity
public class PinterestSettings extends BaseModel {

    @Column
    private String username;

    @Column(length = 16)
    private byte[] passwordSalt;

    @Column
    private byte[] password;

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] salt) {
        this.passwordSalt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String pinterestUsername) {
        this.username = pinterestUsername;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] pinterestPassword) {
        this.password = pinterestPassword;
    }

    public boolean isPinterestSetup() {
        return username != null
                && password != null;
    }
}
