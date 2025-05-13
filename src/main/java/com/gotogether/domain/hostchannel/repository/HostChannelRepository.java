package com.gotogether.domain.hostchannel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.entity.HostChannelStatus;
import com.gotogether.domain.user.entity.User;

@Repository
public interface HostChannelRepository extends JpaRepository<HostChannel, Long> {

	Optional<HostChannel> findByName(String name);

	@Query("""
		SELECT DISTINCT h
		FROM HostChannel h
		JOIN FETCH h.channelOrganizers co
		JOIN FETCH co.user
		WHERE co.user = :user
		AND h.status != :status
		""")
	List<HostChannel> findActiveHostChannelsByUser(User user, HostChannelStatus status);

	@Query("""
		SELECT h
		FROM HostChannel h
		WHERE (:keyword IS NOT NULL
		AND LOWER(TRIM(h.name)) LIKE LOWER(CONCAT('%', :keyword, '%')))
		""")
	Page<HostChannel> findHostChannelByFilter(String keyword, Pageable pageable);
}