package com.gotogether.domain.channelorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.user.entity.User;

@Repository
public interface ChannelOrganizerRepository extends JpaRepository<ChannelOrganizer, Long> {
	void deleteByHostChannel(HostChannel hostChannel);

	long countByHostChannel(HostChannel hostChannel);

	boolean existsByUserAndHostChannel(User user, HostChannel hostChannel);
}
