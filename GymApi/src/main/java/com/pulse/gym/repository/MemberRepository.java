package com.pulse.gym.repository;
import com.pulse.gym.model.GymMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface MemberRepository extends JpaRepository<GymMember,Long> {
    Optional<GymMember> findByDni(String dni);
    boolean existsByDni(String dni);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select m from GymMember m where m.id = :id")
    Optional<GymMember> lockById(@Param("id") Long id);
}
