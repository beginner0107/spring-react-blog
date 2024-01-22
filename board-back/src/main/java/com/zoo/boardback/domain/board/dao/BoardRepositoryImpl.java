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
import com.zoo.boardback.domain.board.dto.response.object.PostRankItem;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
            titleLike(condition.getTitle()),
            contentLike(condition.getContent()),
            commentContentLike(condition.getCommentCont()),
            titleAndContentLike(condition.getTitleAndContent()),
            nicknameLike(condition.getNickname())
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
        .leftJoin(board).on(board.boardNumber.eq(comment.board.boardNumber))
        .where(
            titleLike(condition.getTitle()),
            contentLike(condition.getContent()),
            commentContentLike(condition.getCommentCont()),
            titleAndContentLike(condition.getTitleAndContent()),
            nicknameLike(condition.getNickname())
        );
    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public List<PostRankItem> getTop3Posts(LocalDateTime startDate, LocalDateTime endDate) {
    return queryFactory
        .select(
            constructor(PostRankItem.class,
                board.boardNumber, board.title,
                board.content, image.imageUrl.as("boardTitleImage"),
                board.favoriteCount, board.commentCount,
                board.viewCount, board.user.nickname.as("writerNickname"),
                board.createdAt.as("writerCreatedAt"),
                board.user.profileImage.as("writerProfileImage")
            )
        )
        .from(board)
        .join(board.user, user)
        .leftJoin(image)
        .on(
            image.board.boardNumber.eq(board.boardNumber)
                .and(image.titleImageYn.isTrue())
        )
        .where(board.createdAt.between(startDate, endDate))
        .limit(3)
        .orderBy(
            board.favoriteCount.desc(),
            board.commentCount.desc(),
            board.viewCount.desc(),
            board.createdAt.desc()
        )
        .fetch();
  }

  private BooleanExpression titleLike(String title) {
    return hasText(title) ? board.title.like(likeQuery(title)) : null;
  }

  private BooleanExpression contentLike(String content) {
    return hasText(content) ? board.content.like(likeQuery(content)) : null;
  }

  private BooleanExpression commentContentLike(String commentContent) {
    return hasText(commentContent) ? comment.content.like(likeQuery(commentContent)) : null;
  }

  private BooleanExpression titleAndContentLike(String titleAndContent) {
    return hasText(titleAndContent) ? Objects.requireNonNull(titleLike(titleAndContent))
        .or(contentLike(titleAndContent)) : null;
  }

  private BooleanExpression nicknameLike(String nickname) {
    return hasText(nickname) ? user.nickname.like(likeQuery(nickname)) : null;
  }

  private String likeQuery(String word) {
    return "%" + word + "%";
  }


}
