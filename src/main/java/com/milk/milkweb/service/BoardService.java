package com.milk.milkweb.service;


import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;


	public Board saveBoard(BoardFormDto boardFormDto, String email) {
		Member member = memberRepository.findByEmail(email);
		Board board = Board.builder()
					.title(boardFormDto.getTitle())
					.content(boardFormDto.getContent())
					.createdTime(LocalDateTime.now())
					.views(0)
					.member(member)
					.build();

		return boardRepository.save(board);
	}

	@Transactional(readOnly = true)
	public Page<BoardListDto> getBoardList(int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createdTime"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Page<Board> boards = boardRepository.findAll(pageable);

		Page<BoardListDto> boardListDtos = boards.map(board -> BoardListDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.memberName(board.getMember().getName())
				.views(board.getViews())
				.likes(board.getLikes().size())
				.createdTime(board.getCreatedTime())
				.build());

		return boardListDtos;
	}

	@Transactional(readOnly = true)
	public Page<BoardListDto> getSearchBoardList(BoardSearchDto boardSearchDto, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		return boardRepository.findBySearch(boardSearchDto, pageable);
	}


	@Transactional
	public BoardDetailDto getDetail(Long id) {
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		board.increaseView();

		BoardDetailDto boardDetailDto = BoardDetailDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.content(board.getContent())
				.memberName(board.getMember().getName())
				.createdTime(board.getCreatedTime())
				.updatedTime(board.getUpdatedTime())
				.view(board.getViews())
				.likes(board.getLikes().size())
				.build();

		return boardDetailDto;
	}

	@Transactional(readOnly = true)
	public BoardUpdateDto getUpdateForm(Long id, String email) throws Exception {
		Board board = validateMemberBoard(id, email);

		BoardUpdateDto boardUpdateDto = BoardUpdateDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.content(board.getContent())
				.build();
		return boardUpdateDto;
	}

	public Board updateBoard(BoardUpdateDto boardUpdateDto, Long id) {
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		board.update(boardUpdateDto);
		return board;
	}

	public void deleteBoard(Long id, String email) throws IllegalAccessException {
		Board board = validateMemberBoard(id, email);
		boardRepository.delete(board);
	}

	public Board validateMemberBoard(Long id, String email) throws IllegalAccessException {
		Member member = memberRepository.findByEmail(email);
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		if (member != board.getMember()) {
			throw new IllegalAccessException("권한이 없습니다.");
		}

		return board;
	}

	@Transactional(readOnly = true)
	public List<MainBoardDto> getMainBoard() {
		List<Board> boards = boardRepository.findMainBoards();

		List<MainBoardDto> mainBoardList = boards.stream().map(board -> MainBoardDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.build()).toList();

		return mainBoardList;
	}

}
