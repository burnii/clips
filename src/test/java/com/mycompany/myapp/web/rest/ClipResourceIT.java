package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Clip;
import com.mycompany.myapp.repository.ClipRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ClipResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClipResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_POSITIVE_COUNT = 1;
    private static final Integer UPDATED_POSITIVE_COUNT = 2;

    private static final Integer DEFAULT_NEGATIVE_COUNT = 1;
    private static final Integer UPDATED_NEGATIVE_COUNT = 2;

    private static final String ENTITY_API_URL = "/api/clips";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClipRepository clipRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClipMockMvc;

    private Clip clip;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clip createEntity(EntityManager em) {
        Clip clip = new Clip()
            .name(DEFAULT_NAME)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE)
            .positiveCount(DEFAULT_POSITIVE_COUNT)
            .negativeCount(DEFAULT_NEGATIVE_COUNT);
        return clip;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clip createUpdatedEntity(EntityManager em) {
        Clip clip = new Clip()
            .name(UPDATED_NAME)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .positiveCount(UPDATED_POSITIVE_COUNT)
            .negativeCount(UPDATED_NEGATIVE_COUNT);
        return clip;
    }

    @BeforeEach
    public void initTest() {
        clip = createEntity(em);
    }

    @Test
    @Transactional
    void createClip() throws Exception {
        int databaseSizeBeforeCreate = clipRepository.findAll().size();
        // Create the Clip
        restClipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clip)))
            .andExpect(status().isCreated());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeCreate + 1);
        Clip testClip = clipList.get(clipList.size() - 1);
        assertThat(testClip.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClip.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testClip.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);
        assertThat(testClip.getPositiveCount()).isEqualTo(DEFAULT_POSITIVE_COUNT);
        assertThat(testClip.getNegativeCount()).isEqualTo(DEFAULT_NEGATIVE_COUNT);
    }

    @Test
    @Transactional
    void createClipWithExistingId() throws Exception {
        // Create the Clip with an existing ID
        clip.setId(1L);

        int databaseSizeBeforeCreate = clipRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clip)))
            .andExpect(status().isBadRequest());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clipRepository.findAll().size();
        // set the field null
        clip.setName(null);

        // Create the Clip, which fails.

        restClipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clip)))
            .andExpect(status().isBadRequest());

        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClips() throws Exception {
        // Initialize the database
        clipRepository.saveAndFlush(clip);

        // Get all the clipList
        restClipMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clip.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))))
            .andExpect(jsonPath("$.[*].positiveCount").value(hasItem(DEFAULT_POSITIVE_COUNT)))
            .andExpect(jsonPath("$.[*].negativeCount").value(hasItem(DEFAULT_NEGATIVE_COUNT)));
    }

    @Test
    @Transactional
    void getClip() throws Exception {
        // Initialize the database
        clipRepository.saveAndFlush(clip);

        // Get the clip
        restClipMockMvc
            .perform(get(ENTITY_API_URL_ID, clip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clip.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.positiveCount").value(DEFAULT_POSITIVE_COUNT))
            .andExpect(jsonPath("$.negativeCount").value(DEFAULT_NEGATIVE_COUNT));
    }

    @Test
    @Transactional
    void getNonExistingClip() throws Exception {
        // Get the clip
        restClipMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClip() throws Exception {
        // Initialize the database
        clipRepository.saveAndFlush(clip);

        int databaseSizeBeforeUpdate = clipRepository.findAll().size();

        // Update the clip
        Clip updatedClip = clipRepository.findById(clip.getId()).get();
        // Disconnect from session so that the updates on updatedClip are not directly saved in db
        em.detach(updatedClip);
        updatedClip
            .name(UPDATED_NAME)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .positiveCount(UPDATED_POSITIVE_COUNT)
            .negativeCount(UPDATED_NEGATIVE_COUNT);

        restClipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClip.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClip))
            )
            .andExpect(status().isOk());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
        Clip testClip = clipList.get(clipList.size() - 1);
        assertThat(testClip.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClip.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testClip.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
        assertThat(testClip.getPositiveCount()).isEqualTo(UPDATED_POSITIVE_COUNT);
        assertThat(testClip.getNegativeCount()).isEqualTo(UPDATED_NEGATIVE_COUNT);
    }

    @Test
    @Transactional
    void putNonExistingClip() throws Exception {
        int databaseSizeBeforeUpdate = clipRepository.findAll().size();
        clip.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clip.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clip))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClip() throws Exception {
        int databaseSizeBeforeUpdate = clipRepository.findAll().size();
        clip.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clip))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClip() throws Exception {
        int databaseSizeBeforeUpdate = clipRepository.findAll().size();
        clip.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clip)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClipWithPatch() throws Exception {
        // Initialize the database
        clipRepository.saveAndFlush(clip);

        int databaseSizeBeforeUpdate = clipRepository.findAll().size();

        // Update the clip using partial update
        Clip partialUpdatedClip = new Clip();
        partialUpdatedClip.setId(clip.getId());

        partialUpdatedClip.positiveCount(UPDATED_POSITIVE_COUNT);

        restClipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClip.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClip))
            )
            .andExpect(status().isOk());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
        Clip testClip = clipList.get(clipList.size() - 1);
        assertThat(testClip.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClip.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testClip.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);
        assertThat(testClip.getPositiveCount()).isEqualTo(UPDATED_POSITIVE_COUNT);
        assertThat(testClip.getNegativeCount()).isEqualTo(DEFAULT_NEGATIVE_COUNT);
    }

    @Test
    @Transactional
    void fullUpdateClipWithPatch() throws Exception {
        // Initialize the database
        clipRepository.saveAndFlush(clip);

        int databaseSizeBeforeUpdate = clipRepository.findAll().size();

        // Update the clip using partial update
        Clip partialUpdatedClip = new Clip();
        partialUpdatedClip.setId(clip.getId());

        partialUpdatedClip
            .name(UPDATED_NAME)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .positiveCount(UPDATED_POSITIVE_COUNT)
            .negativeCount(UPDATED_NEGATIVE_COUNT);

        restClipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClip.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClip))
            )
            .andExpect(status().isOk());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
        Clip testClip = clipList.get(clipList.size() - 1);
        assertThat(testClip.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClip.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testClip.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
        assertThat(testClip.getPositiveCount()).isEqualTo(UPDATED_POSITIVE_COUNT);
        assertThat(testClip.getNegativeCount()).isEqualTo(UPDATED_NEGATIVE_COUNT);
    }

    @Test
    @Transactional
    void patchNonExistingClip() throws Exception {
        int databaseSizeBeforeUpdate = clipRepository.findAll().size();
        clip.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clip.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clip))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClip() throws Exception {
        int databaseSizeBeforeUpdate = clipRepository.findAll().size();
        clip.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clip))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClip() throws Exception {
        int databaseSizeBeforeUpdate = clipRepository.findAll().size();
        clip.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClipMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clip)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Clip in the database
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClip() throws Exception {
        // Initialize the database
        clipRepository.saveAndFlush(clip);

        int databaseSizeBeforeDelete = clipRepository.findAll().size();

        // Delete the clip
        restClipMockMvc
            .perform(delete(ENTITY_API_URL_ID, clip.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Clip> clipList = clipRepository.findAll();
        assertThat(clipList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
