package nl.juraji.imagemanager.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.BitSet;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
@Entity
public class HashData extends BaseModel {

    @Column(length = 10000)
    private byte[] bits;

    @Column
    private Contrast contrast;

    @Transient // Caching var for #getBitSet
    private BitSet bitSet;

    public byte[] getBits() {
        return bits;
    }

    public void setBits(byte[] bits) {
        this.bits = bits;
    }

    public Contrast getContrast() {
        return contrast;
    }

    public void setContrast(Contrast contrast) {
        this.contrast = contrast;
    }

    public BitSet getBitSet() {
        if (bitSet == null && bits != null) {
            bitSet = BitSet.valueOf(bits);
        }
        return bitSet;
    }
}
