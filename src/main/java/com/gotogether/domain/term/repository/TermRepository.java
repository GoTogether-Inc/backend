package com.gotogether.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.term.entity.Term;
import com.gotogether.domain.user.entity.User;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
	boolean existsTermByUser(User user);
}