package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clip;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Clip entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClipRepository extends JpaRepository<Clip, Long> {}
