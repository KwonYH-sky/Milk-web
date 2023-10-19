package com.milk.milkweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_img")
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardImg {

	@Id
	@Column(name = "board_img_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String imgName;

	private String imgOriName;

	private String path;

	private LocalDateTime createdTime;
}
