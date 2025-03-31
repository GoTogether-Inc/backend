package com.gotogether.domain.event.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.event.document.EventSearchDocument;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventSearchDocument, String> {
}