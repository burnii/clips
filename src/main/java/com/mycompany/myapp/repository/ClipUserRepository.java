package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ClipUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ClipUser entity.
 */
@Repository
public interface ClipUserRepository extends JpaRepository<ClipUser, Long> {
    @Query(
        value = "select distinct clipUser from ClipUser clipUser left join fetch clipUser.ratedClips",
        countQuery = "select count(distinct clipUser) from ClipUser clipUser"
    )
    Page<ClipUser> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct clipUser from ClipUser clipUser left join fetch clipUser.ratedClips")
    List<ClipUser> findAllWithEagerRelationships();

    @Query("select clipUser from ClipUser clipUser left join fetch clipUser.ratedClips where clipUser.id =:id")
    Optional<ClipUser> findOneWithEagerRelationships(@Param("id") Long id);
}
