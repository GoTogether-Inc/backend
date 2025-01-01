package com.gotogether.domain.eventhashtag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.eventhashtag.entity.EventHashtag;
import com.gotogether.domain.hashtag.entity.Hashtag;

@Repository
public interface EventHashtagRepository extends JpaRepository<EventHashtag, Long> {
	@Query("SELECT eh.hashtag FROM EventHashtag eh WHERE eh.event = :event")
	List<Hashtag> findHashtagsByEvent(@Param("event") Event event);

	@Query("SELECT COUNT(eh) FROM EventHashtag eh WHERE eh.hashtag = :hashtag")
	int countByHashtag(@Param("hashtag") Hashtag hashtag);

	@Modifying
	@Query("DELETE FROM EventHashtag eh WHERE eh.event = :event")
	void deleteByEvent(@Param("event") Event event);
}
