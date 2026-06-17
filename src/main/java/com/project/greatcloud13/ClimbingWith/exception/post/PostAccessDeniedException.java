package com.project.greatcloud13.ClimbingWith.exception.post;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class PostAccessDeniedException extends BusinessException {
    public PostAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED_POST);
    }
}
