package com.gotogether.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.user.entity.User;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByEventAndUser(Event event, User user);
}