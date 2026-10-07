package com.pulse.gym.repository;
import com.pulse.gym.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByOrderByCreatedAtDesc();
    List<Payment> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
