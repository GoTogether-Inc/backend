package com.gotogether.domain.referencelink.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
import com.gotogether.domain.referencelink.entity.ReferenceLink;
import com.gotogether.domain.referencelink.repository.ReferenceLinkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferenceLinkServiceImpl implements ReferenceLinkService {

	private final ReferenceLinkRepository referenceLinkRepository;

	@Override
	@Transactional
	public void createReferenceLinks(Event event, List<ReferenceLinkDTO> referenceLinks) {
		List<ReferenceLink> referenceLinkList = referenceLinks.stream()
			.map(link -> ReferenceLink.builder()
				.event(event)
				.name(link.getTitle())
				.toGoUrl(link.getUrl())
				.build())
			.collect(Collectors.toList());

		referenceLinkRepository.saveAll(referenceLinkList);
	}

	@Override
	@Transactional
	public void deleteAll(List<ReferenceLink> referenceLinks) {
		referenceLinkRepository.deleteAll(referenceLinks);
	}
}
