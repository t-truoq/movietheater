package org.example.movie.repository;

import org.example.movie.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount_AccountId(Long accountId);

    Optional<Member> findById(Long memberId);
}