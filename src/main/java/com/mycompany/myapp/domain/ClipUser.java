package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ClipUser.
 */
@Entity
@Table(name = "clip_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClipUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(unique = true)
    private User internalUser;

    @OneToMany(mappedBy = "creator")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "creator", "ratedUsers" }, allowSetters = true)
    private Set<Clip> clips = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_clip_user__rated_clips",
        joinColumns = @JoinColumn(name = "clip_user_id"),
        inverseJoinColumns = @JoinColumn(name = "rated_clips_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "creator", "ratedUsers" }, allowSetters = true)
    private Set<Clip> ratedClips = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClipUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public ClipUser internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public Set<Clip> getClips() {
        return this.clips;
    }

    public void setClips(Set<Clip> clips) {
        if (this.clips != null) {
            this.clips.forEach(i -> i.setCreator(null));
        }
        if (clips != null) {
            clips.forEach(i -> i.setCreator(this));
        }
        this.clips = clips;
    }

    public ClipUser clips(Set<Clip> clips) {
        this.setClips(clips);
        return this;
    }

    public ClipUser addClip(Clip clip) {
        this.clips.add(clip);
        clip.setCreator(this);
        return this;
    }

    public ClipUser removeClip(Clip clip) {
        this.clips.remove(clip);
        clip.setCreator(null);
        return this;
    }

    public Set<Clip> getRatedClips() {
        return this.ratedClips;
    }

    public void setRatedClips(Set<Clip> clips) {
        this.ratedClips = clips;
    }

    public ClipUser ratedClips(Set<Clip> clips) {
        this.setRatedClips(clips);
        return this;
    }

    public ClipUser addRatedClips(Clip clip) {
        this.ratedClips.add(clip);
        clip.getRatedUsers().add(this);
        return this;
    }

    public ClipUser removeRatedClips(Clip clip) {
        this.ratedClips.remove(clip);
        clip.getRatedUsers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClipUser)) {
            return false;
        }
        return id != null && id.equals(((ClipUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClipUser{" +
            "id=" + getId() +
            "}";
    }
}
