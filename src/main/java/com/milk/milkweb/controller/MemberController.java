package com.milk.milkweb.controller;

import com.milk.milkweb.dto.MailDto;
import com.milk.milkweb.dto.MemberFormDto;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.EmailService;
import com.milk.milkweb.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
		if(bindingResult.hasErrors()) return "member/memberForm";

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

	@PostMapping(value = "/findPwd/sendMail")
	public ResponseEntity<?> sendTempPwdMail(String email) {
		String tempPwd = emailService.getTempPassword();
		try {
			MailDto mailDto = MailDto.toTempPwdMailDto(email, tempPwd);
			emailService.sendMail(mailDto);
			memberService.updatePassword(email, tempPwd, passwordEncoder);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error("MemberController sendTempPwdMail() error: " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
}
