package com.gotogether.domain.event.document;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Document(indexName = "event_keyword")
public class EventSearchDocument {

	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private String keyword;

	@Field(type = FieldType.Date)
	private LocalDateTime timestamp;

	@Builder
	public EventSearchDocument(String keyword, LocalDateTime timestamp) {
		this.id = UUID.randomUUID().toString();
		this.keyword = keyword;
		this.timestamp = timestamp;
	}
}