package org.example.movie.repository;

import org.example.movie.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.account.identityCard = :identityCard")
    Optional<Member> findByIdentityCard(@Param("identityCard") String identityCard);

    @Query("SELECT m FROM Member m WHERE m.account.phoneNumber = :phoneNumber")
    Optional<Member> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    Optional<Member> findByAccount_AccountId(Long accountId);
}