package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.global.error.ErrorCode.COMMENT_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.COMMENT_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_FOUND;

import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.dto.query.ChildCommentQueryDto;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void create(CommentCreateRequestDto requestDto, User user) {
        Long postId = requestDto.getPostId();
        Post post = findPostByPostId(postId);

        Comment child = requestDto.toEntity(user, post);
        requestDto.getCommentIdOptional().ifPresent(commentId -> linkParent(commentId, child));
        commentRepository.save(child);

        post.increaseCommentCount();
    }

    private void linkParent(Long commentId, Comment child) {
        findParentCommentOfChild(commentId)
            .ifPresent(parent -> parent.addChild(child));
    }

    public CommentListResponseDto getComments(Long postId, Pageable pageable) {
        Post post = findPostByPostId(postId);
        Page<CommentQueryDto> comments = commentRepository.getComments(post, pageable);
        return CommentListResponseDto.from(comments);
    }

    @Transactional
    public void update(String email, Long commentId, CommentUpdateRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
            () -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
        checkCommenterMatching(email, comment);
        comment.edit(requestDto);
    }

    @Transactional
    public void delete(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
            () -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
        checkCommenterMatching(email, comment);
        comment.delete();
    }

    public CommentListResponseDto getChildComments(Long postId, Long commentId) {
        findPostByPostId(postId);
        List<ChildCommentQueryDto> comments = commentRepository.getChildComments(postId,
            commentId);
        return CommentListResponseDto.of(comments);
    }

    private Post findPostByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new BusinessException(postId, "postId", POST_NOT_FOUND));
    }

    private void checkCommenterMatching(String email, Comment comment) {
        if (!comment.getUser().getEmail().equals(email)) {
            throw new BusinessException(comment, "comment", COMMENT_NOT_CUD_MATCHING_USER);
        }
    }

    private Optional<Comment> findParentCommentOfChild(Long parentId) {
        return Optional.ofNullable(commentRepository.findById(parentId)
            .orElseThrow(() -> new BusinessException(parentId, "commentId", COMMENT_NOT_FOUND)));
    }
}
