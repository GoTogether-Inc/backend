package com.gotogether.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByIdAndIsDeletedFalse(Long userId);

	Optional<User> findByEmailAndIsDeletedFalse(String email);
}