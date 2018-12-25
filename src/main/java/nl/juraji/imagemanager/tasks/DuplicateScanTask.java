package nl.juraji.imagemanager.tasks;

import com.google.common.util.concurrent.AtomicDouble;
import nl.juraji.imagemanager.fxml.controls.DuplicateSet;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.hashes.HashData;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;

import java.util.*;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class DuplicateScanTask extends IndicatorTask<List<DuplicateSet>> {
    private final BaseDirectory directory;
    private final double minSimilarity;

    public DuplicateScanTask(BaseDirectory directory, double minSimilarity) {
        super("Scanning duplicates");
        this.directory = directory;
        this.minSimilarity = (minSimilarity > 1 ? minSimilarity / 100.0 : minSimilarity);
    }

    @Override
    protected List<DuplicateSet> call() {
        setTotalWork(directory.getTotalMetaDataCount());
        return this.scanForDuplicates(directory);
    }

    private void updateMessage(BaseDirectory d) {
        updateMessage("Scanning duplicates for %s", d.getName());
    }

    @SuppressWarnings("unchecked")
    private List<DuplicateSet> scanForDuplicates(BaseDirectory parent) {
        updateMessage(parent);
        final ArrayList<DuplicateSet> duplicateSets = new ArrayList<>();

        final Set<BaseMetaData> directoryMetaData = parent.getMetaData();
        final ArrayList<BaseMetaData> compareQueue = new ArrayList<>(directoryMetaData);

        directoryMetaData.stream()
                .peek(i -> incrementProgress())
                .peek(i -> this.checkCanceled())
                .map(a -> {
                    final AtomicDouble addedSimilarity = new AtomicDouble(0.0);
                    final List<BaseMetaData> similarMetaData = new ArrayList<>();

                    for (BaseMetaData b : compareQueue) {
                        if (!b.equals(a)) {
                            final double similarity = this.compareHashes(a, b);
                            if (similarity > 0) {
                                addedSimilarity.addAndGet(similarity);
                                similarMetaData.add(b);
                            }
                        }
                    }

                    if (similarMetaData.size() > 0) {
                        final int averageSimilarity = (int) (addedSimilarity.get() / similarMetaData.size());
                        compareQueue.remove(a);
                        compareQueue.removeAll(similarMetaData);
                        similarMetaData.add(a);
                        return new DuplicateSet(parent, similarMetaData, averageSimilarity);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(duplicateSets::add);

        this.checkCanceled();
        final Set<BaseDirectory> children = parent.getChildren();
        if (children != null) {
            for (BaseDirectory child : children) {
                duplicateSets.addAll(this.scanForDuplicates(child));
            }
        }

        return duplicateSets;
    }

    /**
     * Where the contrast of image a and b are equal,
     * calculate the cardinality between a and b using an XOR operation on the
     * sets of bytes, calculated by {@link HashDirectoryTask}.
     * The cardinality of an XOR operation on two bit sets is the count of unequal bits.
     * Check if the cardinality is below a threshold, stating the images are mostly equal.
     *
     * @param a The meta data of the origin image
     * @param b The meta data if the compared image
     * @return The percentage of similarity
     */
    private double compareHashes(BaseMetaData a, BaseMetaData b) {
        final HashData ah = a.getHash();
        final HashData bh = b.getHash();

        if (ah != null && bh != null) {
            if (Objects.equals(ah.getContrast(), bh.getContrast())) {
                BitSet xor = (BitSet) ah.getBitSet().clone();
                xor.xor(bh.getBitSet());
                int similarBitCount = xor.length() - xor.cardinality();
                double minSimilarBitCount = xor.length() * minSimilarity;
                if (similarBitCount > minSimilarBitCount) {
                    return (similarBitCount / (double) xor.length()) * 100.0;
                }
            }
        }

        return -1.0;
    }
}
