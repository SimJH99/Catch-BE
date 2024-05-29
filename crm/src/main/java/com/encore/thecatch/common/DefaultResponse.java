package com.encore.thecatch.common;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString(doNotUseGetters = true)
public class DefaultResponse<T> {

    //@Schema(required = true, example = "{}", description = "데이터")
    private T data;

    @Builder
    public DefaultResponse(T data) {
        this.data = data;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PagedResponse<T> {

        private int totalPage;
        private Long count;
        //@Schema(required = true, example = "[]", description = "데이터")
        private List<T> data;

        @Builder
        public PagedResponse(Page<T> page) {
            this.totalPage = page.getTotalPages();
            this.count = page.getTotalElements();
            this.data = page.getContent();
        }

        @Builder
        public PagedResponse(Long count, List<T> data) {
            this.count = count;
            this.data = data;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ListResponse<T> {

        private Long count;
        //@Schema(required = true, example = "[]", description = "데이터")
        private List<T> data;

        @Builder
        public ListResponse(List<T> list) {
            this.count = (long) list.size();
            this.data = list;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorResponse<T> {

        //@Schema(required = true, example = "CELL_EXISTS", description = "코드")
        private T error;

        @Builder
        public ErrorResponse(T error) {
            this.error = error;
        }
    }
}

