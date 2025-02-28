package com.gotogether.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.user.entity.User;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByEventAndUser(Event event, User user);

    @Query("SELECT b FROM Bookmark b JOIN FETCH b.event e LEFT JOIN FETCH e.eventHashtags WHERE b.user.id = :userId")
    List<Bookmark> findByUserId(@Param("userId") Long userId);
}