package com.pulse.gym.repository;
import com.pulse.gym.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
public interface CheckInRepository extends JpaRepository<CheckIn,Long> {
    List<CheckIn> findByEnteredAtGreaterThanEqualOrderByEnteredAtDesc(Instant since);
    List<CheckIn> findTop10ByOrderByEnteredAtDesc();
    List<CheckIn> findTop20ByMemberIdOrderByEnteredAtDesc(Long memberId);
}
