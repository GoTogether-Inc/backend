package com.gotogether.domain.referencelink.service;

import java.util.List;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
import com.gotogether.domain.referencelink.entity.ReferenceLink;

public interface ReferenceLinkService {
	void createReferenceLinks(Event event, List<ReferenceLinkDTO> referenceLinks);

	void deleteAll(List<ReferenceLink> referenceLinks);
}
