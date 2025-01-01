package com.gotogether.domain.referencelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gotogether.domain.referencelink.entity.ReferenceLink;

public interface ReferenceLinkRepository extends JpaRepository<ReferenceLink, Long> {

}
