package com.gotogether.domain.hostchannel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.user.entity.User;

@Repository
public interface HostChannelRepository extends JpaRepository<HostChannel, Long> {
	Optional<HostChannel> findByIdAndIsDeletedFalse(Long hostChannelId);

	@Query("SELECT h FROM HostChannel h " + "JOIN h.channelOrganizers co " + "WHERE co.user = :user")
	Page<HostChannel> findByUser(@Param("user") User user, Pageable pageable);
}