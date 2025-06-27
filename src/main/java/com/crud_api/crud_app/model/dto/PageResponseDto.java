package com.crud_api.crud_app.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponseDto<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;

}