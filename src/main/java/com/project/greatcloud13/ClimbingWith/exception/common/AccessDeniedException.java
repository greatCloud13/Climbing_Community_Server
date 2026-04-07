package com.project.greatcloud13.ClimbingWith.exception.common;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException() {
        super(ErrorCode.AccessDenied);
    }
}
