package com.milk.milkweb.service;

import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.dto.OAuthAttributes;
import com.milk.milkweb.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private DefaultOAuth2UserService oAuth2UserService;

	@InjectMocks
	private CustomOAuth2UserService customOAuth2UserService;

	@Test
	@DisplayName("OAuth2 로그인 테스트")
	void loadUserTest() {
		// given
		OAuth2UserRequest mockRequest = mock(OAuth2UserRequest.class);
		OAuth2User mockOAuth2User = mock(OAuth2User.class);

		ClientRegistration mockClientRegistration = mock(ClientRegistration.class);
		ProviderDetails mockProviderDetails = mock(ProviderDetails.class);
		UserInfoEndpoint mockUserInfoEndpoint = mock(UserInfoEndpoint.class);

		given(oAuth2UserService.loadUser(any())).willReturn(mockOAuth2User);

		given(mockRequest.getClientRegistration()).willReturn(mockClientRegistration);
		given(mockClientRegistration.getRegistrationId()).willReturn("google");
		given(mockClientRegistration.getProviderDetails()).willReturn(mockProviderDetails);
		given(mockProviderDetails.getUserInfoEndpoint()).willReturn(mockUserInfoEndpoint);

		given(mockClientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).willReturn("sub");

		OAuthAttributes attributes = OAuthAttributes.builder()
				.email("test@test")
				.name("김우유")
				.attributes(Map.of("test", "테스트"))
				.build();

		given(memberRepository.findByEmail(null)).willReturn(null);
		given(memberRepository.save(any())).willReturn(attributes.toEntity());

		// when
		CustomUserDetails result = (CustomUserDetails) customOAuth2UserService.loadUser(mockRequest);

		// then
		assertThat(result).as("존재 여부").isNotNull();
		verify(memberRepository, times(1)).save(any());
	}
}