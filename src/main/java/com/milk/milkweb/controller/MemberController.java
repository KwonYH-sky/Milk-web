package com.milk.milkweb.controller;

import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.dto.MailDto;
import com.milk.milkweb.dto.MailPwdSendDto;
import com.milk.milkweb.dto.MemberFormDto;
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

	@GetMapping(value = "/findPwd")
	public String findPwd() {
		return "member/memberFindPwdForm";
	}

	@PostMapping(value = "/findPwd/sendMail")
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

	@GetMapping(value = "/myPage")
	public String toMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return "member/memberMyPage";
	}

	@PostMapping
	public ResponseEntity<?> changeName(@AuthenticationPrincipal CustomUserDetails userDetails, String newName) {
		try {
			memberService.updateName(userDetails.getName(), newName);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error("MemberController changeName() error : " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping
	public ResponseEntity<?> validatePwd(@AuthenticationPrincipal CustomUserDetails userDetails, String password) {
		if (memberService.validatePassword(userDetails, passwordEncoder.encode(password))){
			return new ResponseEntity<>(HttpStatus.OK);
		} else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@PostMapping
	public ResponseEntity<?> changePwd(@AuthenticationPrincipal CustomUserDetails userDetails, String newPwd) {
		try {
			memberService.updatePassword(userDetails.getName(), newPwd, passwordEncoder);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error("MemberController changePwd() error : " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
}
