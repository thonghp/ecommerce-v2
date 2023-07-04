package com.hpt.common.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInfo {
    private int totalPages;
    private long totalElements;

    public long getStartCount(int pageNum, int pageSize) {
        return (long) (pageNum - 1) * pageSize + 1;
    }

    public long getEndCount(int pageNum, int pageSize) {
        long endCount = getStartCount(pageNum, pageSize) + pageSize - 1;

        if (endCount > getTotalElements()) endCount = getTotalElements();

        return endCount;
    }
}
