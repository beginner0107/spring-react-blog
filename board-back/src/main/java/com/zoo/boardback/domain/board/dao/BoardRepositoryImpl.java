package com.zoo.boardback.domain.board.dao;

import static com.querydsl.core.types.Projections.constructor;
import static com.zoo.boardback.domain.board.entity.QBoard.board;
import static com.zoo.boardback.domain.comment.entity.QComment.comment;
import static com.zoo.boardback.domain.image.entity.QImage.image;
import static com.zoo.boardback.domain.user.entity.QUser.user;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.board.entity.QBoard;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class BoardRepositoryImpl implements BoardRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  public BoardRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<PostSearchResponseDto> searchPosts(PostSearchCondition condition, Pageable pageable) {
    List<PostSearchResponseDto> content = queryFactory
        .select(constructor(PostSearchResponseDto.class,
            user.profileImage, user.nickname,
            board.createdAt, board.title,
            board.content, board.viewCount,
            board.favoriteCount, board.commentCount,
            ExpressionUtils.as(
                JPAExpressions
                    .select(image.imageUrl)
                    .from(image)
                    .where(image.titleImageYn.isTrue()
                        .and(image.board.boardNumber.eq(board.boardNumber))
                    ), "boardTitleImage"
            )
        ))
        .from(board)
        .join(board.user, user)
        .leftJoin(board).on(board.boardNumber.eq(comment.board.boardNumber))
        .where(
            titleEq(condition.getTitle()),
            contentEq(condition.getContent()),
            commentContentEq(condition.getCommentCont()),
            titleAndContentEq(condition.getTitleAndContent()),
            nicknameEq(condition.getNickname())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(
            board.createdAt.desc()
        )
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(board.count())
        .from(board)
        .where(
            titleEq(condition.getTitle()),
            contentEq(condition.getContent()),
            commentContentEq(condition.getCommentCont()),
            titleAndContentEq(condition.getTitleAndContent()),
            nicknameEq(condition.getNickname())
        );
    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanExpression titleEq(String title) {
    return hasText(title) ? board.title.eq(title) : null;
  }

  private BooleanExpression contentEq(String content) {
    return hasText(content) ? board.content.eq(content) : null;
  }

  private BooleanExpression commentContentEq(String commentContent) {
    return hasText(commentContent) ? comment.content.eq(commentContent) : null;
  }

  private BooleanExpression titleAndContentEq(String titleAndContent) {
    return hasText(titleAndContent) ? titleEq(titleAndContent).or(contentEq(titleAndContent)) : null;
  }

  private BooleanExpression nicknameEq(String nickname) {
    return hasText(nickname) ? user.nickname.eq(nickname) : null;
  }


}
