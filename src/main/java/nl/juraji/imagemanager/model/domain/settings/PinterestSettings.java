package nl.juraji.imagemanager.model.domain.settings;

import io.ebean.annotation.Encrypted;
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

    @Column
    @Encrypted
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPinterestSetup() {
        return username != null
                && password != null;
    }
}
