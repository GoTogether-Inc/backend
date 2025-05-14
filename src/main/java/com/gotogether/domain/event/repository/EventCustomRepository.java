package com.gotogether.domain.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.event.entity.Event;

public interface EventCustomRepository {

	Page<Event> findEventsByTag(String tag, Pageable pageable);
}