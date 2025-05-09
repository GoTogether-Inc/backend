package com.gotogether.domain.channelorganizer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.user.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ChannelOrganizerRepository extends JpaRepository<ChannelOrganizer, Long> {
	long countByHostChannel(HostChannel hostChannel);

	boolean existsByUserAndHostChannel(User user, HostChannel hostChannel);

	@Query("""
		SELECT co FROM ChannelOrganizer co
		JOIN FETCH co.user
		WHERE co.hostChannel = :hostChannel
		""")
	List<ChannelOrganizer> findChannelOrganizerWithUserByHostChannel(@Param("hostChannel") HostChannel hostChannel);
}
