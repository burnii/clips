package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClipUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClipUser.class);
        ClipUser clipUser1 = new ClipUser();
        clipUser1.setId(1L);
        ClipUser clipUser2 = new ClipUser();
        clipUser2.setId(clipUser1.getId());
        assertThat(clipUser1).isEqualTo(clipUser2);
        clipUser2.setId(2L);
        assertThat(clipUser1).isNotEqualTo(clipUser2);
        clipUser1.setId(null);
        assertThat(clipUser1).isNotEqualTo(clipUser2);
    }
}
