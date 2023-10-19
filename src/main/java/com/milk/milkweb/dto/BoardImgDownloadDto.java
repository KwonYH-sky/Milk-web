package com.milk.milkweb.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Getter @Builder
public class BoardImgDownloadDto {

	private Resource resource;

	private HttpHeaders header;
}
