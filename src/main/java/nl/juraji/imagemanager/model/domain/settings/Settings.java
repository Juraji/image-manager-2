package nl.juraji.imagemanager.model.domain.settings;

import nl.juraji.imagemanager.model.domain.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.nio.file.Path;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
@Entity
public class Settings extends BaseModel {

    @Column(length = 2048)
    private Path defaultTargetDirectory;

    @Column
    private int duplicateScannerMinSimilarity;

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
}
