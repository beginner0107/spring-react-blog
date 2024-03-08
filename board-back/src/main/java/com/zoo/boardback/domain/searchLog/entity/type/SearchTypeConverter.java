package com.zoo.boardback.domain.searchLog.entity.type;

import jakarta.persistence.AttributeConverter;

public class SearchTypeConverter implements AttributeConverter<SearchType, String> {

    @Override
    public String convertToDatabaseColumn(SearchType searchType) {
        if (searchType == null) {
            return null;
        }
        return searchType.getSearchType();
    }

    @Override
    public SearchType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return SearchType.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
