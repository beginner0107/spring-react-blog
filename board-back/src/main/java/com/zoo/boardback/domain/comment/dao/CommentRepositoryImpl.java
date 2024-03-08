package com.zoo.boardback.domain.comment.dao;

import static com.querydsl.core.types.Projections.constructor;
import static com.zoo.boardback.domain.comment.entity.QComment.comment;
import static com.zoo.boardback.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zoo.boardback.domain.comment.dto.query.ChildCommentQueryDto;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.comment.entity.QComment;
import com.zoo.boardback.domain.post.entity.Post;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final String SELF_COMMENT = "selfComment";
    private final String CHILD_COUNT_COLUMN_NAME = "childCount";

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CommentQueryDto> getComments(Post post, Pageable pageable) {
        QComment selfComment = new QComment(SELF_COMMENT);

        List<CommentQueryDto> comments = queryFactory
            .select(constructor(CommentQueryDto.class,
                    comment.id,
                    user.nickname,
                    user.profileImage,
                    comment.content,
                    comment.createdAt,
                    comment.updatedAt,
                    selfComment.parent.count().as(CHILD_COUNT_COLUMN_NAME),
                    comment.delYn
                )
            )
            .from(comment)
            .leftJoin(selfComment).on(comment.id.eq(selfComment.parent.id))
            .join(comment.user, user)
            .where(
                comment.post.id.eq(post.getId())
                    .and(comment.parent.id.isNull())
            )
            .groupBy(comment.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(comment.createdAt.desc())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(comment.count())
            .leftJoin(selfComment).on(comment.id.eq(selfComment.parent.id))
            .where(
                comment.post.id.eq(post.getId())
                    .and(comment.parent.id.isNull())
            )
            .groupBy(comment.id)
            .from(comment);
        return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
    }

    @Override
    public List<ChildCommentQueryDto> getChildComments(Long postId, Long parentId) {
        QComment selfComment = new QComment(SELF_COMMENT);
        return queryFactory
            .select(constructor(ChildCommentQueryDto.class,
                    comment.id,
                    user.nickname,
                    user.profileImage,
                    comment.content,
                    comment.createdAt,
                    comment.updatedAt,
                    selfComment.parent.count().as(CHILD_COUNT_COLUMN_NAME),
                    comment.delYn
                )
            )
            .from(comment)
            .leftJoin(selfComment).on(comment.id.eq(selfComment.parent.id))
            .join(comment.user, user)
            .where(
                comment.post.id.eq(postId)
                    .and(comment.parent.id.eq(parentId))
            )
            .groupBy(comment.id)
            .orderBy(comment.createdAt.desc())
            .fetch();
    }
}
