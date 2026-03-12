package com.hilary.leedtech.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private int status;
    private String timestamp;
    private String path;
}
