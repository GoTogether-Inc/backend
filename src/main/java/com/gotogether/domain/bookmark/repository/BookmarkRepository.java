package com.gotogether.domain.bookmark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.user.entity.User;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	boolean existsByEventAndUser(Event event, User user);

	@Query("""
		    SELECT DISTINCT b
		    FROM Bookmark b
		    JOIN FETCH b.event e
		    JOIN FETCH e.hostChannel hc
		    LEFT JOIN FETCH e.eventHashtags eh
		    LEFT JOIN FETCH eh.hashtag h
		    WHERE b.user.id = :userId
		""")
	List<Bookmark> findByUserId(@Param("userId") Long userId);

	Optional<Bookmark> findByEventAndUser(Event event, User user);
}