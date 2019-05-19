package nl.juraji.imagemanager.model.domain;

import nl.juraji.imagemanager.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class DefaultDirectory extends BaseDirectory<DefaultDirectory, BaseMetaData> {
    private Set<BaseMetaData> metaData;

    public DefaultDirectory() {
        super(DirectoryOrigin.DEFAULT);
    }

    @Override
    public Set<DefaultDirectory> getChildren() {
        return Collections.emptySet();
    }

    @Override
    public Set<BaseMetaData> getMetaData() {
        metaData = CollectionUtils.init(metaData);
        return metaData;
    }

    @Override
    public DefaultDirectory getParent() {
        return null;
    }

    @Override
    public void setParent(DefaultDirectory directory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMetaDataCount() {
        return metaData == null ? 0 : metaData.size();
    }

    @Override
    public Integer getTotalMetaDataCount() {
        return getMetaDataCount();
    }
}
