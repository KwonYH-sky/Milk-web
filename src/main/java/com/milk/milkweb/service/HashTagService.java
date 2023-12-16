package com.milk.milkweb.service;


import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.BoardHashTag;
import com.milk.milkweb.entity.HashTag;
import com.milk.milkweb.repository.BoardHashTagRepository;
import com.milk.milkweb.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HashTagService {

	private final HashTagRepository hashTagRepository;
	private final BoardHashTagRepository boardHashTagRepository;


	public void saveHashTag(Set<String> dtoTags, Board board) {
		if (dtoTags.isEmpty()) return;

		List<HashTag> hashTags = dtoTags.stream()
				.filter(s -> !hashTagRepository.existsByTag(s))
				.map(HashTag::of)
				.toList();
		log.info("saveHashTag -> hashTagRepository.saveAll");
		hashTagRepository.saveAll(hashTags);

		List<BoardHashTag> boardHashTags = dtoTags.stream()
				.map(hashTagRepository::findByTag)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(tag -> BoardHashTag.of(board, tag))
				.toList();
		log.info("saveHashTag -> boardHashTagRepository.saveAll");
		boardHashTagRepository.saveAll(boardHashTags);
	}


	@Transactional(readOnly = true)
	public Set<String> readHashTag(Long boardId) {
		return boardHashTagRepository.findAllByBoardId(boardId).stream()
				.map(boardHashTag -> boardHashTag.getHashTag().getTag())
				.collect(Collectors.toSet());
	}

	public void DeleteBoardTag(Long boardId, Set<String> dtoTags) {
		List<BoardHashTag> boardHashTags = boardHashTagRepository.findAllByBoardId(boardId);

		List<BoardHashTag> deleteBoardTags = boardHashTags.stream()
				.filter(t -> !dtoTags.contains(t.getHashTag().getTag()))
				.toList();
		boardHashTagRepository.deleteAll(deleteBoardTags);

	}
}
