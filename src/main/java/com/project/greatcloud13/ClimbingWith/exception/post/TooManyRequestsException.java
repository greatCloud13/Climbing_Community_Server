package com.project.greatcloud13.ClimbingWith.exception.post;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class TooManyRequestsException extends BusinessException {
    public TooManyRequestsException() {
        super(ErrorCode.TO_MANY_REQUEST_POST);
    }
}
