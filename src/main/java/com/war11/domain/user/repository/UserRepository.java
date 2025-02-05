package com.war11.domain.user.repository;

import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.entity.User;
import com.war11.global.config.CustomUserDetails;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    @Query("SELECT new com.war11.domain.user.dto.response.UserResponse(a.id, a.loginId,a.name) FROM User a WHERE a.id = :id")
    Page<UserResponse> findAllUsers(@Param("id") Long id,Pageable pageable);
}
