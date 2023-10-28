package com.milk.milkweb.controller;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.EmailService;
import com.milk.milkweb.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/member")
@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

	private final MemberService memberService;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;


	@GetMapping(value = "/register")
	public String entryMemberForm(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());
		return "member/memberForm";
	}

	@PostMapping(value = "/register")
	public String registerMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) return "member/memberForm";

		try {
			Member member = Member.createMember(memberFormDto, passwordEncoder);
			memberService.saveMember(member);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "member/memberForm";
		}

		return "redirect:/";
	}

	@GetMapping(value = "/login")
	public String loginMember() {
		return "member/memberLoginForm";
	}

	@GetMapping(value = "/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "이메일 또는 비밀번호가 일치하지 않습니다.");
		return "member/memberLoginForm";
	}

	@GetMapping(value = "/find-pwd")
	public String findPwd() {
		return "member/memberFindPwdForm";
	}

	@PostMapping(value = "/find-pwd/send-mail")
	public ResponseEntity<?> sendTempPwdMail(@RequestBody MailPwdSendDto mailPwdSendDto) {
		String tempPwd = emailService.getTempPassword();
		try {
			memberService.updatePassword(mailPwdSendDto.getEmail(), tempPwd, passwordEncoder);
			MailDto mailDto = MailDto.toTempPwdMailDto(mailPwdSendDto.getEmail(), tempPwd);
			emailService.sendMail(mailDto);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error("MemberController sendTempPwdMail() error: " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/mypage")
	public String toMyPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		model.addAttribute("MemberName", userDetails.getUsername());
		return "member/memberMyPage";
	}

	@PostMapping("/mypage/change-name")
	public ResponseEntity<?> changeName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid MyPageNameDto myPageNameDto) {
		try {
			memberService.updateName(userDetails.getName(), myPageNameDto.getName());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error("MemberController changeName() error : " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/mypage/modify-info")
	public String toModifyPwd(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		model.addAttribute("isSocial", userDetails.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals(Role.SOCIAL.getKey())));
		return "member/memberModifyPwd";
	}

	@PostMapping("/mypage/validate-pwd")
	public ResponseEntity<?> validatePwd(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPagePasswordDto myPagePasswordDto) {
		if (memberService.validatePassword(userDetails, myPagePasswordDto.getPassword(), passwordEncoder)){
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/mypage/modify-info")
	public ResponseEntity<?> changePwd(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid MyPagePasswordDto myPagePasswordDto) {
		try {
			memberService.updatePassword(userDetails.getName(), myPagePasswordDto.getPassword(), passwordEncoder);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error("MemberController changePwd() error : " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
}
