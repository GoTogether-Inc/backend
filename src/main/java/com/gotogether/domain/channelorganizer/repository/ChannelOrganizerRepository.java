package com.gotogether.domain.channelorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;

@Repository
public interface ChannelOrganizerRepository extends JpaRepository<ChannelOrganizer, Long> {
}
