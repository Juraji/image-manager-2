package nl.juraji.imagemanager.model.domain.settings;

import nl.juraji.imagemanager.model.domain.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
@Entity
public class Settings extends BaseModel {

    @Column
    private int duplicateScannerMinSimilarity;

    public int getDuplicateScannerMinSimilarity() {
        return duplicateScannerMinSimilarity;
    }

    public void setDuplicateScannerMinSimilarity(int duplicateScannerMinimalSimilarity) {
        this.duplicateScannerMinSimilarity = duplicateScannerMinimalSimilarity;
    }
}
