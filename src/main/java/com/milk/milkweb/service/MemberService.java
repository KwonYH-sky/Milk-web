package com.milk.milkweb.service;

import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.AlreadyRegisterException;
import com.milk.milkweb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;

	public Member saveMember(Member member) {
		validateDuplicateMember(member);
		return memberRepository.save(member);
	}

	public void validateDuplicateMember(Member member)  {
		Member findMember = memberRepository.findByEmail(member.getEmail());
		if (findMember != null) {
			throw new AlreadyRegisterException("이미 가입된 이메일입니다.");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = Optional.ofNullable(memberRepository.findByEmail(email)).orElseThrow(() -> new UsernameNotFoundException(email));

		return new CustomUserDetails(member);
	}
}
