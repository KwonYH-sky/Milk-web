package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.AlreadyRegisterException;
import com.milk.milkweb.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
		if (validateDuplicateName(member.getName())) {
			throw new AlreadyRegisterException("이미 등록된 이름입니다.");
		}
		return memberRepository.save(member);
	}

	@Transactional(readOnly = true)
	public void validateDuplicateMember(Member member) {
		Member findMember = memberRepository.findByEmail(member.getEmail());
		if (findMember != null) {
			throw new AlreadyRegisterException("이미 가입된 이메일입니다.");
		}
	}

	@Transactional(readOnly = true)
	public boolean validateDuplicateName(String name) {
		return memberRepository.existsByName(name);
	}

	public boolean validatePassword(CustomUserDetails userDetails, String password, PasswordEncoder passwordEncoder) {

		return passwordEncoder.matches(password, userDetails.getPassword());
	}


	public void updateName(String email, String newName) {
		if (validateDuplicateName(newName)) {
			throw new AlreadyRegisterException("이미 등록된 이름입니다.");
		}
		Member member = Optional.ofNullable(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);
		member.updateName(newName);
	}

	public void updatePassword(String email, String newPwd, PasswordEncoder passwordEncoder) {
		Member member = Optional.ofNullable(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);
		if (member.getRoleKey().equals(Role.SOCIAL))
			member.toUSER();
		member.updatePassword(newPwd, passwordEncoder);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = Optional.ofNullable(memberRepository.findByEmail(email)).orElseThrow(() -> new UsernameNotFoundException(email));

		return new CustomUserDetails(member);
	}
}
