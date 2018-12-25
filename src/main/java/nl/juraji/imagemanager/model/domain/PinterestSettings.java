package nl.juraji.imagemanager.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Juraji on 25-12-2018.
 * Image Manager 2
 */
@Entity
public class PinterestSettings extends BaseModel {

    @Column
    private String pinterestUsername;

    @Column(length = 16)
    private byte[] passwordSalt;

    @Column
    private byte[] pinterestPassword;

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] salt) {
        this.passwordSalt = salt;
    }

    public String getPinterestUsername() {
        return pinterestUsername;
    }

    public void setPinterestUsername(String pinterestUsername) {
        this.pinterestUsername = pinterestUsername;
    }

    public byte[] getPinterestPassword() {
        return pinterestPassword;
    }

    public void setPinterestPassword(byte[] pinterestPassword) {
        this.pinterestPassword = pinterestPassword;
    }

    public boolean isPinterestSetup() {
        return pinterestUsername != null
                && pinterestPassword != null;
    }
}
