package com.gotogether.domain.event.service;

import java.io.IOException;
import java.util.List;

public interface EventSearchService {
	void saveSearchKeyword(String keyword);

	List<String> getPopularKeywords() throws IOException;
}
