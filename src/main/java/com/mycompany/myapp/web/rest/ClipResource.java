package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Clip;
import com.mycompany.myapp.repository.ClipRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Clip}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClipResource {

    private final Logger log = LoggerFactory.getLogger(ClipResource.class);

    private static final String ENTITY_NAME = "clip";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClipRepository clipRepository;

    public ClipResource(ClipRepository clipRepository) {
        this.clipRepository = clipRepository;
    }

    /**
     * {@code POST  /clips} : Create a new clip.
     *
     * @param clip the clip to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clip, or with status {@code 400 (Bad Request)} if the clip has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clips")
    public ResponseEntity<Clip> createClip(@Valid @RequestBody Clip clip) throws URISyntaxException {
        log.debug("REST request to save Clip : {}", clip);
        if (clip.getId() != null) {
            throw new BadRequestAlertException("A new clip cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Clip result = clipRepository.save(clip);
        return ResponseEntity
            .created(new URI("/api/clips/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clips/:id} : Updates an existing clip.
     *
     * @param id the id of the clip to save.
     * @param clip the clip to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clip,
     * or with status {@code 400 (Bad Request)} if the clip is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clip couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clips/{id}")
    public ResponseEntity<Clip> updateClip(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Clip clip)
        throws URISyntaxException {
        log.debug("REST request to update Clip : {}, {}", id, clip);
        if (clip.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clip.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Clip result = clipRepository.save(clip);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clip.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /clips/:id} : Partial updates given fields of an existing clip, field will ignore if it is null
     *
     * @param id the id of the clip to save.
     * @param clip the clip to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clip,
     * or with status {@code 400 (Bad Request)} if the clip is not valid,
     * or with status {@code 404 (Not Found)} if the clip is not found,
     * or with status {@code 500 (Internal Server Error)} if the clip couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clips/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Clip> partialUpdateClip(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Clip clip
    ) throws URISyntaxException {
        log.debug("REST request to partial update Clip partially : {}, {}", id, clip);
        if (clip.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clip.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Clip> result = clipRepository
            .findById(clip.getId())
            .map(existingClip -> {
                if (clip.getName() != null) {
                    existingClip.setName(clip.getName());
                }
                if (clip.getContent() != null) {
                    existingClip.setContent(clip.getContent());
                }
                if (clip.getContentContentType() != null) {
                    existingClip.setContentContentType(clip.getContentContentType());
                }
                if (clip.getPositiveCount() != null) {
                    existingClip.setPositiveCount(clip.getPositiveCount());
                }
                if (clip.getNegativeCount() != null) {
                    existingClip.setNegativeCount(clip.getNegativeCount());
                }

                return existingClip;
            })
            .map(clipRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clip.getId().toString())
        );
    }

    /**
     * {@code GET  /clips} : get all the clips.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clips in body.
     */
    @GetMapping("/clips")
    public List<Clip> getAllClips() {
        log.debug("REST request to get all Clips");
        return clipRepository.findAll();
    }

    /**
     * {@code GET  /clips/:id} : get the "id" clip.
     *
     * @param id the id of the clip to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clip, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clips/{id}")
    public ResponseEntity<Clip> getClip(@PathVariable Long id) {
        log.debug("REST request to get Clip : {}", id);
        Optional<Clip> clip = clipRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clip);
    }

    /**
     * {@code DELETE  /clips/:id} : delete the "id" clip.
     *
     * @param id the id of the clip to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clips/{id}")
    public ResponseEntity<Void> deleteClip(@PathVariable Long id) {
        log.debug("REST request to delete Clip : {}", id);
        clipRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
