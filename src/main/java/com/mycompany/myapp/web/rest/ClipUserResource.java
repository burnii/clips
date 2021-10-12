package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ClipUser;
import com.mycompany.myapp.repository.ClipUserRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ClipUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClipUserResource {

    private final Logger log = LoggerFactory.getLogger(ClipUserResource.class);

    private static final String ENTITY_NAME = "clipUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClipUserRepository clipUserRepository;

    public ClipUserResource(ClipUserRepository clipUserRepository) {
        this.clipUserRepository = clipUserRepository;
    }

    /**
     * {@code POST  /clip-users} : Create a new clipUser.
     *
     * @param clipUser the clipUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clipUser, or with status {@code 400 (Bad Request)} if the clipUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clip-users")
    public ResponseEntity<ClipUser> createClipUser(@RequestBody ClipUser clipUser) throws URISyntaxException {
        log.debug("REST request to save ClipUser : {}", clipUser);
        if (clipUser.getId() != null) {
            throw new BadRequestAlertException("A new clipUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClipUser result = clipUserRepository.save(clipUser);
        return ResponseEntity
            .created(new URI("/api/clip-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clip-users/:id} : Updates an existing clipUser.
     *
     * @param id the id of the clipUser to save.
     * @param clipUser the clipUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clipUser,
     * or with status {@code 400 (Bad Request)} if the clipUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clipUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clip-users/{id}")
    public ResponseEntity<ClipUser> updateClipUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClipUser clipUser
    ) throws URISyntaxException {
        log.debug("REST request to update ClipUser : {}, {}", id, clipUser);
        if (clipUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clipUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clipUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClipUser result = clipUserRepository.save(clipUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clipUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /clip-users/:id} : Partial updates given fields of an existing clipUser, field will ignore if it is null
     *
     * @param id the id of the clipUser to save.
     * @param clipUser the clipUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clipUser,
     * or with status {@code 400 (Bad Request)} if the clipUser is not valid,
     * or with status {@code 404 (Not Found)} if the clipUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the clipUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clip-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClipUser> partialUpdateClipUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClipUser clipUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClipUser partially : {}, {}", id, clipUser);
        if (clipUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clipUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clipUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClipUser> result = clipUserRepository
            .findById(clipUser.getId())
            .map(existingClipUser -> {
                return existingClipUser;
            })
            .map(clipUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clipUser.getId().toString())
        );
    }

    /**
     * {@code GET  /clip-users} : get all the clipUsers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clipUsers in body.
     */
    @GetMapping("/clip-users")
    public List<ClipUser> getAllClipUsers(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all ClipUsers");
        return clipUserRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /clip-users/:id} : get the "id" clipUser.
     *
     * @param id the id of the clipUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clipUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clip-users/{id}")
    public ResponseEntity<ClipUser> getClipUser(@PathVariable Long id) {
        log.debug("REST request to get ClipUser : {}", id);
        Optional<ClipUser> clipUser = clipUserRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(clipUser);
    }

    /**
     * {@code DELETE  /clip-users/:id} : delete the "id" clipUser.
     *
     * @param id the id of the clipUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clip-users/{id}")
    public ResponseEntity<Void> deleteClipUser(@PathVariable Long id) {
        log.debug("REST request to delete ClipUser : {}", id);
        clipUserRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
