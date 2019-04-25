package nl.juraji.imagemanager.model.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import org.jetbrains.annotations.NotNull;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
@MappedSuperclass
public abstract class BaseModel extends Model implements Comparable<BaseModel> {

    @Id
    @GeneratedValue()
    private UUID id;

    @Version
    private Long version;

    @WhenCreated
    private Instant created;

    @WhenModified
    private Instant modified;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof BaseMetaData)) {
            return false;
        } else {
            return id != null && id == ((BaseMetaData) o).getId();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public int compareTo(@NotNull BaseModel o) {
        if (this.getId() == null) {
            return -1;
        } else if (o.getId() == null) {
            return 1;
        } else {
            return Objects.compare(this.getId(), o.getId(), UUID::compareTo);
        }
    }
}
