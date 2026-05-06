package com.nearprop.repository;

import com.nearprop.entity.FranchiseeBankDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FranchiseeBankDetailRepository extends JpaRepository<FranchiseeBankDetail, Long> {
    List<FranchiseeBankDetail> findByUserId(Long userId);
    
    Optional<FranchiseeBankDetail> findByUserIdAndIsPrimaryTrue(Long userId);
    
    boolean existsByUserIdAndAccountNumber(Long userId, String accountNumber);
}
