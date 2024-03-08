package com.zoo.boardback.domain.searchLog.dao;

import static com.querydsl.core.types.Projections.constructor;
import static com.zoo.boardback.domain.searchLog.entity.QSearchLog.searchLog;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zoo.boardback.domain.searchLog.dto.query.PopularSearchWordDto;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SearchLogRepositoryImpl implements SearchLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    public SearchLogRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<PopularSearchWordDto> getPopularSearchWords(SearchType searchType) {
        return queryFactory
            .select(
                constructor(PopularSearchWordDto.class,
                    searchLog.searchWord, searchLog.count().as("count")
                ))
            .from(searchLog)
            .where(
                searchTypeEq(searchType)
            )
            .groupBy(searchLog.searchWord)
            .orderBy(
                searchLog.count().desc(),
                searchLog.searchWord.asc()
            )
            .limit(10)
            .fetch();
    }

    private BooleanExpression searchTypeEq(SearchType searchType) {
        return searchType != null ? searchLog.searchType.eq(searchType) : null;
    }
}
