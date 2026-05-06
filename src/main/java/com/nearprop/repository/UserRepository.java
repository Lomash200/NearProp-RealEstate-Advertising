//package com.nearprop.repository;
//
//import com.nearprop.entity.Role;
//import com.nearprop.entity.Subscription;
//import com.nearprop.entity.SubscriptionPlan;
//import com.nearprop.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByMobileNumber(String mobileNumber);
//    Optional<User> findByEmail(String email);
//    Optional<User> findByPermanentId(String permanentId);
//    boolean existsByMobileNumber(String mobileNumber);
//    boolean existsByEmail(String email);
//    boolean existsByPermanentId(String permanentId);
//
//    // Added method to find user by username (using mobile number as username)
//    default Optional<User> findByUsername(String username) {
//        return findByMobileNumber(username);
//    }
//    @Query("""
//SELECT DISTINCT u
//FROM User u
//JOIN u.roles r
//JOIN u.subscriptions s
//JOIN s.plan p
//WHERE r = :role
//AND p.type = :planType
//AND s.status = :status
//AND s.endDate > CURRENT_TIMESTAMP
//""")
//    List<User> findPublicUsersWithActiveProfile(
//            @Param("role") Role role,
//            @Param("planType") SubscriptionPlan.PlanType planType,
//            @Param("status") Subscription.SubscriptionStatus status
//    );
//
//
//
//    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.district = :district")
//    List<User> findAllByRoleAndDistrict(Role role, String district);
//
//    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE r = :role AND u.district = :district")
//    boolean existsByRoleAndDistrict(Role role, String district);
//
//    @Query("SELECT u FROM User u WHERE u.district IN :districts")
//    List<User> findByDistrictIn(@Param("districts") List<String> districts);
//
//    List<User> findByDistrictId(Long districtId);
//    List<User> findDistinctByRolesContaining(Role role);
//
//    @Query("SELECT u.fcmToken FROM User u WHERE u.fcmToken IS NOT NULL")
//     List<String> findAllFcmTokens();
//
//
//    @Query("SELECT u FROM User u WHERE u.districtId IN :districtIds")
//    List<User> findByDistrictIdIn(@Param("districtIds") List<Long> districtIds);
//
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.SELLER")
//    Long countSellerUsers();
//
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.ADVISOR")
//    Long countAdvisorUsers();
//
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.DEVELOPER")
//    Long countDeveloperUsers();
//
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.FRANCHISEE")
//    Long countFranchiseeUsers();
//
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.SUBADMIN")
//    Long countSubadminUsers();
//
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.ADMIN")
//    Long countAdminUsers();
//
//
//    @Query("""
//SELECT DISTINCT u.fcmToken
//FROM User u
//JOIN u.subscriptions s
//WHERE u.districtId = :districtId
//AND u.fcmToken IS NOT NULL
//AND s.status = :status
//AND s.endDate > CURRENT_TIMESTAMP
//""")
//    List<String> findPropertyNotificationTokens(
//            @Param("districtId") Long districtId,
//            @Param("status") Subscription.SubscriptionStatus status
//    );
//
//
//    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
//    List<User> findAllByRole(@Param("role") Role role);
//
//}

package com.nearprop.repository;

import com.nearprop.entity.Role;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByPermanentId(String permanentId);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByPermanentId(String permanentId);

    // Added method to find user by username (using mobile number as username)
    default Optional<User> findByUsername(String username) {
        return findByMobileNumber(username);
    }

//    @Query("""
//        SELECT DISTINCT u
//        FROM Subscription s
//        JOIN s.user u
//        JOIN s.plan p
//        WHERE :role MEMBER OF u.roles
//          AND s.status = :status
//          AND p.type_s = :typeS
//    """)
//    List<User> findActiveUsersByRoleAndPlanType(
//            @Param("role") Role role,
//            @Param("status") Subscription.SubscriptionStatus status,
//            @Param("typeS") String typeS
//    );

    @Query("""
        SELECT DISTINCT u
        FROM User u
        WHERE :role MEMBER OF u.roles
          AND EXISTS (
              SELECT 1
              FROM Subscription s
              JOIN s.plan p
              WHERE s.user = u
                AND s.status = :status
                AND p.type_s = :typeS
          )
    """)
    List<User> findUsersHavingActiveProfileSubscription(
            @Param("role") Role role,
            @Param("status") Subscription.SubscriptionStatus status,
            @Param("typeS") String typeS
    );


    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.district = :district")
    List<User> findAllByRoleAndDistrict(Role role, String district);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE r = :role AND u.district = :district")
    boolean existsByRoleAndDistrict(Role role, String district);

    @Query("SELECT u FROM User u WHERE u.district IN :districts")
    List<User> findByDistrictIn(@Param("districts") List<String> districts);

    List<User> findByDistrictId(Long districtId);
    List<User> findDistinctByRolesContaining(Role role);

    @Query("SELECT u.fcmToken FROM User u WHERE u.fcmToken IS NOT NULL")
    List<String> findAllFcmTokens();


    @Query("SELECT u FROM User u WHERE u.districtId IN :districtIds")
    List<User> findByDistrictIdIn(@Param("districtIds") List<Long> districtIds);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.SELLER")
    Long countSellerUsers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.ADVISOR")
    Long countAdvisorUsers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.DEVELOPER")
    Long countDeveloperUsers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.FRANCHISEE")
    Long countFranchiseeUsers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.SUBADMIN")
    Long countSubadminUsers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = com.nearprop.entity.Role.ADMIN")
    Long countAdminUsers();


    @Query("""
SELECT DISTINCT u.fcmToken
FROM User u
JOIN u.subscriptions s
WHERE u.districtId = :districtId
AND u.fcmToken IS NOT NULL
AND s.status = :status
AND s.endDate > CURRENT_TIMESTAMP
""")
    List<String> findPropertyNotificationTokens(
            @Param("districtId") Long districtId,
            @Param("status") Subscription.SubscriptionStatus status
    );


    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findAllByRole(@Param("role") Role role);

}