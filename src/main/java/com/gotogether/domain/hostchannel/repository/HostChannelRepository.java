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
	@Query("SELECT h FROM HostChannel h " + "JOIN h.channelOrganizers co " + "WHERE co.user = :user"
		+ " AND h.status != com.gotogether.domain.hostchannel.entity.HostChannelStatus.INACTIVE")
	Page<HostChannel> findByUser(@Param("user") User user, Pageable pageable);

	Optional<HostChannel> findByName(String name);

	@Query("SELECT h FROM HostChannel h " + "JOIN h.channelOrganizers co " + "WHERE h.name = :name AND co.user = :user")
	Optional<HostChannel> findByNameAndUser(String name, User user);

	@Query("SELECT h FROM HostChannel h " +
		"WHERE (:keyword IS NOT NULL AND " +
		"LOWER(TRIM(h.name)) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<HostChannel> findHostChannelByFilter(String keyword, Pageable pageable);
}