package com.zoo.boardback.domain.comment.dao;

import static com.querydsl.core.types.Projections.constructor;
import static com.zoo.boardback.domain.comment.entity.QComment.comment;
import static com.zoo.boardback.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CommentRepositoryImpl implements CommentRepositoryCustom{
  private final JPAQueryFactory queryFactory;

  public CommentRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<CommentQueryDto> getCommentsList(Board board, Pageable pageable) {
    List<CommentQueryDto> comments = queryFactory
        .select(constructor(CommentQueryDto.class,
                comment.commentNumber,
                user.nickname,
                user.profileImage,
                comment.content,
                comment.createdAt,
                comment.updatedAt
            )
        )
        .from(comment)
        .join(comment.user, user)
        .where(comment.board.boardNumber.eq(board.getBoardNumber()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(comment.createdAt.desc())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(comment.count())
        .from(comment);
    return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
  }
}
