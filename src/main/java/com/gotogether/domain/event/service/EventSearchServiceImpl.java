package com.gotogether.domain.event.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gotogether.domain.event.document.EventSearchDocument;
import com.gotogether.domain.event.repository.EventSearchRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {

	private final EventSearchRepository searchKeywordRepository;
	private final ElasticsearchClient client;

	public void saveSearchKeyword(String keyword) {
		EventSearchDocument eventSearchDocument = EventSearchDocument.builder()
			.keyword(keyword)
			.timestamp(LocalDateTime.now())
			.build();

		searchKeywordRepository.save(eventSearchDocument);
	}

	public List<String> getPopularKeywords() throws IOException {
		SearchResponse<Void> searchResponse = client.search(s -> s
			.index("event_keyword")
			.size(0)
			.aggregations("top_keywords", a -> a
				.terms(t -> t.field("keyword").size(5))
			), Void.class);

		return searchResponse.aggregations().get("top_keywords")
			.sterms().buckets().array().stream()
			.map(bucket -> bucket.key().stringValue())
			.collect(Collectors.toList());
	}
}