package nl.juraji.imagemanager.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.nio.file.Path;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
@Entity
public class Settings extends BaseModel {

    @Column(length = 16)
    private String cipherBase;

    @Column
    private String pinterestUsername;

    @Column
    private byte[] pinterestPassword;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(length = 2048)
    private Path defaultTargetDirectory;

    @Column
    private int duplicateScannerMinSimilarity;

    public String getCipherBase() {
        return cipherBase;
    }

    public void setCipherBase(String cipherBase) {
        this.cipherBase = cipherBase;
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

    public Path getDefaultTargetDirectory() {
        return defaultTargetDirectory;
    }

    public void setDefaultTargetDirectory(Path defaultTargetDirectory) {
        this.defaultTargetDirectory = defaultTargetDirectory;
    }

    public int getDuplicateScannerMinSimilarity() {
        return duplicateScannerMinSimilarity;
    }

    public void setDuplicateScannerMinSimilarity(int duplicateScannerMinimalSimilarity) {
        this.duplicateScannerMinSimilarity = duplicateScannerMinimalSimilarity;
    }

    public boolean isPinterestSetup() {
        return pinterestUsername != null
                && pinterestPassword != null;
    }
}
