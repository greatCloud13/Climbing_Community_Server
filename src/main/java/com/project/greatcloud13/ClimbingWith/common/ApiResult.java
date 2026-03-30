package com.project.greatcloud13.ClimbingWith.common;

public record ApiResult<T>(
        boolean success,
        T data,
        ApiError error
) {
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(true, data, null);
    }

    public static ApiResult<?> fail(String code, String message) {
        return new ApiResult<>(false, null, new ApiError(code, message));
    }
}

