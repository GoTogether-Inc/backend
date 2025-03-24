package com.gotogether.domain.referencelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.referencelink.entity.ReferenceLink;

@Repository
public interface ReferenceLinkRepository extends JpaRepository<ReferenceLink, Long> {

}
