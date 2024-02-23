package com.zoo.boardback.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // AUTH
  TOKEN_NOT_AVAILABLE("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
  TOKEN_EXPIRED("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

  // USER
  USER_NOT_FOUND("해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_EMAIL_DUPLICATE("회원 이메일이 중복됩니다.", HttpStatus.CONFLICT),
  USER_LOGIN_ID_DUPLICATE("회원 닉네임이 중복됩니다.", HttpStatus.CONFLICT),
  USER_LOGIN_TEL_NUMBER_DUPLICATE("회원 전화번호가 중복됩니다.", HttpStatus.CONFLICT),
  USER_WRONG_ID_OR_PASSWORD("아이디 혹은 비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST),
  USER_WRONG_PASSWORD("비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST),

  // POST
  POST_NOT_FOUND("해당 게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  POST_CONTENT_NEED("게시글의 내용을 입력해주세요.", HttpStatus.BAD_REQUEST),
  POST_COMMENT_NEED("게시글의 댓글 작성이 필요합니다.", HttpStatus.BAD_REQUEST),
  POST_NOT_CUD_MATCHING_USER("게시글 작성자만 게시글을 변경 가능합니다.", HttpStatus.BAD_REQUEST),

  // COMMENT
  COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_WRITER("댓글 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),
  COMMENT_NOT_CUD_MATCHING_USER("댓글 작성자만 댓글을 변경 가능합니다.", HttpStatus.BAD_REQUEST),

  // DB
  DATABASE_ERROR("데이터베이스 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // FAVORITE
  FAVORITE_CANCEL("좋아요 취소를 할 수 없습니다.", HttpStatus.BAD_REQUEST);
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
