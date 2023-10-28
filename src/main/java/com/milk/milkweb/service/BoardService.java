package com.milk.milkweb.service;


import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.MemberValidationException;
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
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;


	public Board saveBoard(BoardFormDto boardFormDto, String email) {
		Member member = Optional.ofNullable(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);
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
	public BoardDetailDto getDetail(Long id, Member member) {
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
				.isAuthorized(board.getMember().getId().equals(member.getId()) || member.getRoleKey().equals(Role.ADMIN.getKey()))
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

	public void deleteBoard(Long id, String email) {
		Board board = validateMemberBoard(id, email);
		boardRepository.delete(board);
	}

	public Board validateMemberBoard(Long id, String email) {
		Member member = memberRepository.findByEmail(email);
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		if (member != board.getMember() && !(member.getRoleKey().equals(Role.ADMIN.getKey()))) {
			throw new MemberValidationException("권한이 없습니다.");
		}

		return board;
	}

	@Transactional(readOnly = true)
	public List<MainBoardDto> getMainBoard() {
		List<Board> boards = boardRepository.findMainBoards();
		List<MainBoardDto> mainBoardList = boards.stream().map(board -> MainBoardDto.of(board)).toList();
		return mainBoardList;
	}

	@Transactional(readOnly = true)
	public List<MainBoardDto> getDailyBest() {
		List<Board> boards = boardRepository.findDailyBest();
		return boards.stream().map(board -> MainBoardDto.of(board)).toList();
	}

	@Transactional(readOnly = true)
	public List<MainBoardDto> getWeelyBest() {
		List<Board> boards = boardRepository.findWeeklyBest();
		return boards.stream().map(board -> MainBoardDto.of(board)).toList();
	}
}
