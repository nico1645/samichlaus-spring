package com.samichlaus.api.exception;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @Builder.Default
    private Date timestamp = new Date();
    private Integer status;
    private String message;
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    private String path;
}
