package com.milk.milkweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {

	private String address;

	private String title;

	private String message;

	public static MailDto toTempPwdMailDto (String email, String tempPwd) {
		return MailDto.builder()
				.address(email)
				.title("Milk Web 임시비밀번호 안내 메일입니다.")
				.message("회원의 임시 비밀번호는 " + tempPwd + " 입니다.")
				.build();
	}
}
