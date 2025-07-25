package com.gotogether.domain.event.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hostchannel.entity.HostChannel;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("""
		SELECT e FROM Event e
		WHERE (:keyword IS NOT NULL AND LOWER(TRIM(e.title)) LIKE LOWER(CONCAT('%', :keyword, '%')))
				AND e.endDate >= CURRENT_TIMESTAMP
		ORDER BY e.createdAt DESC
		""")
	Page<Event> findEventsByFilter(@Param("keyword") String keyword, Pageable pageable);

	@Query("""
			SELECT e FROM Event e
			WHERE e.category = :category
				AND e.endDate >= CURRENT_TIMESTAMP
				AND e.status != 'DELETED'
			ORDER BY e.createdAt DESC
		""")
	Page<Event> findByCategory(Category category, Pageable pageable);

	long countByHostChannel(HostChannel hostChannel);

	@Query("""
		SELECT DISTINCT e FROM Event e
		LEFT JOIN FETCH e.eventHashtags eh
		LEFT JOIN FETCH eh.hashtag
		WHERE e.hostChannel.id = :hostChannelId
		AND e.status != 'DELETED'
		ORDER BY e.createdAt DESC
		""")
	List<Event> findAllByHostChannelId(@Param("hostChannelId") Long hostChannelId);
}