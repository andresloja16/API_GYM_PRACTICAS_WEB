package com.pulse.gym.repository;
import com.pulse.gym.model.GymUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<GymUser,Long> {
    Optional<GymUser> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String code);
}
