package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ClipUser;
import com.mycompany.myapp.repository.ClipUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClipUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClipUserResourceIT {

    private static final String ENTITY_API_URL = "/api/clip-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClipUserRepository clipUserRepository;

    @Mock
    private ClipUserRepository clipUserRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClipUserMockMvc;

    private ClipUser clipUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClipUser createEntity(EntityManager em) {
        ClipUser clipUser = new ClipUser();
        return clipUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClipUser createUpdatedEntity(EntityManager em) {
        ClipUser clipUser = new ClipUser();
        return clipUser;
    }

    @BeforeEach
    public void initTest() {
        clipUser = createEntity(em);
    }

    @Test
    @Transactional
    void createClipUser() throws Exception {
        int databaseSizeBeforeCreate = clipUserRepository.findAll().size();
        // Create the ClipUser
        restClipUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clipUser)))
            .andExpect(status().isCreated());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeCreate + 1);
        ClipUser testClipUser = clipUserList.get(clipUserList.size() - 1);
    }

    @Test
    @Transactional
    void createClipUserWithExistingId() throws Exception {
        // Create the ClipUser with an existing ID
        clipUser.setId(1L);

        int databaseSizeBeforeCreate = clipUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClipUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clipUser)))
            .andExpect(status().isBadRequest());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClipUsers() throws Exception {
        // Initialize the database
        clipUserRepository.saveAndFlush(clipUser);

        // Get all the clipUserList
        restClipUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clipUser.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClipUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(clipUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClipUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clipUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClipUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clipUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClipUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clipUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getClipUser() throws Exception {
        // Initialize the database
        clipUserRepository.saveAndFlush(clipUser);

        // Get the clipUser
        restClipUserMockMvc
            .perform(get(ENTITY_API_URL_ID, clipUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clipUser.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingClipUser() throws Exception {
        // Get the clipUser
        restClipUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClipUser() throws Exception {
        // Initialize the database
        clipUserRepository.saveAndFlush(clipUser);

        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();

        // Update the clipUser
        ClipUser updatedClipUser = clipUserRepository.findById(clipUser.getId()).get();
        // Disconnect from session so that the updates on updatedClipUser are not directly saved in db
        em.detach(updatedClipUser);

        restClipUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClipUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClipUser))
            )
            .andExpect(status().isOk());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
        ClipUser testClipUser = clipUserList.get(clipUserList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingClipUser() throws Exception {
        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();
        clipUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClipUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clipUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clipUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClipUser() throws Exception {
        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();
        clipUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clipUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClipUser() throws Exception {
        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();
        clipUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clipUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClipUserWithPatch() throws Exception {
        // Initialize the database
        clipUserRepository.saveAndFlush(clipUser);

        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();

        // Update the clipUser using partial update
        ClipUser partialUpdatedClipUser = new ClipUser();
        partialUpdatedClipUser.setId(clipUser.getId());

        restClipUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClipUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClipUser))
            )
            .andExpect(status().isOk());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
        ClipUser testClipUser = clipUserList.get(clipUserList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateClipUserWithPatch() throws Exception {
        // Initialize the database
        clipUserRepository.saveAndFlush(clipUser);

        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();

        // Update the clipUser using partial update
        ClipUser partialUpdatedClipUser = new ClipUser();
        partialUpdatedClipUser.setId(clipUser.getId());

        restClipUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClipUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClipUser))
            )
            .andExpect(status().isOk());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
        ClipUser testClipUser = clipUserList.get(clipUserList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingClipUser() throws Exception {
        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();
        clipUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClipUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clipUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clipUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClipUser() throws Exception {
        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();
        clipUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clipUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClipUser() throws Exception {
        int databaseSizeBeforeUpdate = clipUserRepository.findAll().size();
        clipUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clipUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClipUser in the database
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClipUser() throws Exception {
        // Initialize the database
        clipUserRepository.saveAndFlush(clipUser);

        int databaseSizeBeforeDelete = clipUserRepository.findAll().size();

        // Delete the clipUser
        restClipUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, clipUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClipUser> clipUserList = clipUserRepository.findAll();
        assertThat(clipUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
