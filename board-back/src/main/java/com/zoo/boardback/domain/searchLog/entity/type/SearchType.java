package com.zoo.boardback.domain.searchLog.entity.type;

import static org.springframework.util.StringUtils.hasText;

import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum SearchType {
    POST_WRITER_TITLE("PT"),
    POST_WRITER_CONTENT("PC"),
    POST_WRITER_TITLE_OR_CONTENT("PTC"),
    COMMENT_WRITER_CONTENT("CC"),
    POST_WRITER_NICKNAME("PN");

    private static final Map<SearchType, Function<PostSearchCondition, String>> SEARCH_FUNCTION_MAP;

    static {
        SEARCH_FUNCTION_MAP = Map.of(
            POST_WRITER_NICKNAME, PostSearchCondition::getNickname,
            POST_WRITER_TITLE, PostSearchCondition::getTitle,
            POST_WRITER_CONTENT, PostSearchCondition::getContent,
            POST_WRITER_TITLE_OR_CONTENT, PostSearchCondition::getTitleOrContent,
            COMMENT_WRITER_CONTENT, PostSearchCondition::getCommentCont
        );
    }

    private final String searchType;

    SearchType(String searchType) {
        this.searchType = searchType;
    }

    public static Optional<SearchType> findSearchType(PostSearchCondition condition) {
        return Arrays.stream(values())
            .filter(searchType -> searchType.hasText(condition))
            .findFirst();
    }

    public static Optional<String> findSearchWord(SearchType searchType,
        PostSearchCondition condition) {
        return Optional.ofNullable(searchType.getFunction().apply(condition));
    }

    public static SearchType fromCode(String dbData) {
        return Arrays.stream(values())
            .filter(v -> v.getSearchType().equals(dbData))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("검색 타입에 %s가 존재하지 않습니다.", dbData)));
    }

    public boolean hasText(PostSearchCondition condition) {
        return hasTextFunction().apply(condition);
    }

    private Function<PostSearchCondition, String> getFunction() {
        return SEARCH_FUNCTION_MAP.get(this);
    }

    private Function<PostSearchCondition, Boolean> hasTextFunction() {
        return condition -> hasText(getFunction().apply(condition));
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
