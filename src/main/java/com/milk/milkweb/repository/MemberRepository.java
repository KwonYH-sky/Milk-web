package com.milk.milkweb.repository;

import com.milk.milkweb.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Member findByEmail(String email);
}
