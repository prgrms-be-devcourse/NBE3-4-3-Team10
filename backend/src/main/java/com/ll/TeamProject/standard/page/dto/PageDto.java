package com.ll.TeamProject.standard.page.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
public class PageDto<T> {
    @NonNull
    private int currentPageNumber;
    @NonNull
    private int pageSize;
    @NonNull
    private long totalPages;
    @NonNull
    private long totalItems;
    @NonNull
    private List<T> items;

    public PageDto(Page<T> page) {
        this.currentPageNumber = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
        this.items = page.getContent();
    }
}
