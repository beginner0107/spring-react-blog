package com.zoo.boardback.domain.searchLog.entity;

import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import com.zoo.boardback.domain.searchLog.entity.type.SearchTypeConverter;
import com.zoo.boardback.global.entity.BaseEntity;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "search_log")
@EqualsAndHashCode(of = {"sequence"}, callSuper = false)
public class SearchLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequence;
    @Convert(converter = SearchTypeConverter.class)
    private SearchType searchType;
    private String searchWord;

    @Builder
    public SearchLog(Long sequence, String searchWord, SearchType searchType
    ) {
        this.sequence = sequence;
        this.searchWord = searchWord;
        this.searchType = searchType;
    }

    private SearchLog(SearchType searchType, String searchWord
    ) {
        this.searchType = searchType;
        this.searchWord = searchWord;
    }

    public static SearchLog create(SearchType searchType, String searchWord
    ) {
        return new SearchLog(searchType, searchWord);
    }
}

