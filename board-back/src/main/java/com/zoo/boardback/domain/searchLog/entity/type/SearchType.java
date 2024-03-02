package com.zoo.boardback.domain.searchLog.entity.type;

import static org.springframework.util.StringUtils.hasText;

import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import java.util.Arrays;

public enum SearchType {
    POST_WRITER_TITLE("PT"),
    POST_WRITER_CONTENT("PC"),
    POST_WRITER_TITLE_OR_CONTENT("PTC"),
    COMMENT_WRITER_CONTENT("CC"),
    POST_WRITER_NICKNAME("PN"),
    NOT_EXIST_SEARCH_WORD("NE");

    private final String searchType;

    SearchType(String searchType) {
        this.searchType = searchType;
    }

    public static SearchType findSearchType(PostSearchCondition condition) {
        if (hasText(condition.getNickname())) {
            return POST_WRITER_NICKNAME;
        }
        if (hasText(condition.getTitle())) {
            return POST_WRITER_TITLE;
        }
        if (hasText(condition.getContent())) {
            return POST_WRITER_CONTENT;
        }
        if (hasText(condition.getTitleOrContent())) {
            return POST_WRITER_TITLE_OR_CONTENT;
        }
        if (hasText(condition.getCommentCont())) {
            return COMMENT_WRITER_CONTENT;
        }
        return NOT_EXIST_SEARCH_WORD;
    }

    public static String findSearchWord(SearchType searchType, PostSearchCondition condition) {
        if (searchType == POST_WRITER_NICKNAME) {
            return condition.getNickname();
        }
        if (searchType == POST_WRITER_TITLE) {
            return condition.getTitle();
        }
        if (searchType == POST_WRITER_CONTENT) {
            return condition.getContent();
        }
        if (searchType == POST_WRITER_TITLE_OR_CONTENT) {
            return condition.getTitleOrContent();
        }
        if (searchType == COMMENT_WRITER_CONTENT) {
            return condition.getCommentCont();
        }
        return null;
    }

    public static SearchType fromCode(String dbData) {
        return Arrays.stream(SearchType.values())
            .filter(v -> v.getSearchType().equals(dbData))
            .findAny()
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("검색 타입에 %s가 존재하지 않습니다.", dbData)));
    }

    public String getSearchType() {
        return searchType;
    }
}
