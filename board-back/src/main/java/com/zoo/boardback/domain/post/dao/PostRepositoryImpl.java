package com.zoo.boardback.domain.post.dao;

import static com.querydsl.core.types.Projections.constructor;
import static com.zoo.boardback.domain.comment.entity.QComment.comment;
import static com.zoo.boardback.domain.image.entity.QImage.image;
import static com.zoo.boardback.domain.post.entity.QPost.post;
import static com.zoo.boardback.domain.user.entity.QUser.user;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.post.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.post.dto.response.object.PostRankItem;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostSearchResponseDto> searchPosts(PostSearchCondition condition,
        Pageable pageable) {
        List<PostSearchResponseDto> content = queryFactory
            .select(constructor(PostSearchResponseDto.class,
                user.profileImage, user.nickname,
                post.createdAt, post.title,
                post.content, post.viewCount,
                post.favoriteCount, post.commentCount,
                ExpressionUtils.as(
                    JPAExpressions
                        .select(image.imageUrl)
                        .from(image)
                        .where(image.titleImageYn.isTrue()
                            .and(image.post.id.eq(post.id))
                        ), "boardTitleImage"
                )
            ))
            .from(post)
            .join(post.user, user)
            .leftJoin(post).on(post.id.eq(comment.post.id))
            .where(
                titleLike(condition.getTitle()),
                contentLike(condition.getContent()),
                commentContentLike(condition.getCommentCont()),
                titleAndContentLike(condition.getTitleOrContent()),
                nicknameLike(condition.getNickname())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(
                post.createdAt.desc()
            )
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(post.count())
            .from(post)
            .leftJoin(post).on(post.id.eq(comment.post.id))
            .where(
                titleLike(condition.getTitle()),
                contentLike(condition.getContent()),
                commentContentLike(condition.getCommentCont()),
                titleAndContentLike(condition.getTitleOrContent()),
                nicknameLike(condition.getNickname())
            );
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<PostRankItem> getTop3Posts(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
            .select(
                constructor(PostRankItem.class,
                    post.id, post.title,
                    post.content, image.imageUrl.as("boardTitleImage"),
                    post.favoriteCount, post.commentCount,
                    post.viewCount, post.user.nickname.as("writerNickname"),
                    post.createdAt.as("writerCreatedAt"),
                    post.user.profileImage.as("writerProfileImage")
                )
            )
            .from(post)
            .join(post.user, user)
            .leftJoin(image)
            .on(
                image.post.id.eq(post.id)
                    .and(image.titleImageYn.isTrue())
            )
            .where(post.createdAt.between(startDate, endDate))
            .limit(3)
            .orderBy(
                post.favoriteCount.desc(),
                post.commentCount.desc(),
                post.viewCount.desc(),
                post.createdAt.desc()
            )
            .fetch();
    }

    private BooleanExpression titleLike(String title) {
        return hasText(title) ? post.title.like(likeQuery(title)) : null;
    }

    private BooleanExpression contentLike(String content) {
        return hasText(content) ? post.content.like(likeQuery(content)) : null;
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
