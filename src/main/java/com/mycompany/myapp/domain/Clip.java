package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Clip.
 */
@Entity
@Table(name = "clip")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Clip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @NotNull
    @Column(name = "content_content_type", nullable = false)
    private String contentContentType;

    @Column(name = "positive_count")
    private Integer positiveCount;

    @Column(name = "negative_count")
    private Integer negativeCount;

    @ManyToOne
    @JsonIgnoreProperties(value = { "internalUser", "clips", "ratedClips" }, allowSetters = true)
    private ClipUser creator;

    @ManyToMany(mappedBy = "ratedClips")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "internalUser", "clips", "ratedClips" }, allowSetters = true)
    private Set<ClipUser> ratedUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Clip id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Clip name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return this.content;
    }

    public Clip content(byte[] content) {
        this.setContent(content);
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return this.contentContentType;
    }

    public Clip contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public Integer getPositiveCount() {
        return this.positiveCount;
    }

    public Clip positiveCount(Integer positiveCount) {
        this.setPositiveCount(positiveCount);
        return this;
    }

    public void setPositiveCount(Integer positiveCount) {
        this.positiveCount = positiveCount;
    }

    public Integer getNegativeCount() {
        return this.negativeCount;
    }

    public Clip negativeCount(Integer negativeCount) {
        this.setNegativeCount(negativeCount);
        return this;
    }

    public void setNegativeCount(Integer negativeCount) {
        this.negativeCount = negativeCount;
    }

    public ClipUser getCreator() {
        return this.creator;
    }

    public void setCreator(ClipUser clipUser) {
        this.creator = clipUser;
    }

    public Clip creator(ClipUser clipUser) {
        this.setCreator(clipUser);
        return this;
    }

    public Set<ClipUser> getRatedUsers() {
        return this.ratedUsers;
    }

    public void setRatedUsers(Set<ClipUser> clipUsers) {
        if (this.ratedUsers != null) {
            this.ratedUsers.forEach(i -> i.removeRatedClips(this));
        }
        if (clipUsers != null) {
            clipUsers.forEach(i -> i.addRatedClips(this));
        }
        this.ratedUsers = clipUsers;
    }

    public Clip ratedUsers(Set<ClipUser> clipUsers) {
        this.setRatedUsers(clipUsers);
        return this;
    }

    public Clip addRatedUsers(ClipUser clipUser) {
        this.ratedUsers.add(clipUser);
        clipUser.getRatedClips().add(this);
        return this;
    }

    public Clip removeRatedUsers(ClipUser clipUser) {
        this.ratedUsers.remove(clipUser);
        clipUser.getRatedClips().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Clip)) {
            return false;
        }
        return id != null && id.equals(((Clip) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Clip{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            ", positiveCount=" + getPositiveCount() +
            ", negativeCount=" + getNegativeCount() +
            "}";
    }
}
