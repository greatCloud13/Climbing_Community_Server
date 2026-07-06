package com.project.greatcloud13.ClimbingWith.exception.auth;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class DuplicateFieldException extends BusinessException {
    public DuplicateFieldException(ErrorCode errorCode) {
        super(errorCode);
    }
}
