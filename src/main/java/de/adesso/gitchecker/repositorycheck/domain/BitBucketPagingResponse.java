package de.adesso.gitchecker.repositorycheck.domain;

import lombok.Data;

import java.util.List;

@Data
public class BitBucketPagingResponse<E> {

    private Integer start;
    private Integer size;
    private Integer limit;
    private Boolean isLastPage;

    private List<E> values;

    public Integer getNextStartPage() {
        return start + size;
    }
}
