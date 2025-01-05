package com.gotogether.domain.channelorganizer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.user.entity.User;

@Repository
public interface ChannelOrganizerRepository extends JpaRepository<ChannelOrganizer, Long> {
	long countByHostChannel(HostChannel hostChannel);

	boolean existsByUserAndHostChannel(User user, HostChannel hostChannel);

	List<ChannelOrganizer> findByHostChannel(HostChannel hostChannel);
}
