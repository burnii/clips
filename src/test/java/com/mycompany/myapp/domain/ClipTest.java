package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClipTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Clip.class);
        Clip clip1 = new Clip();
        clip1.setId(1L);
        Clip clip2 = new Clip();
        clip2.setId(clip1.getId());
        assertThat(clip1).isEqualTo(clip2);
        clip2.setId(2L);
        assertThat(clip1).isNotEqualTo(clip2);
        clip1.setId(null);
        assertThat(clip1).isNotEqualTo(clip2);
    }
}
