package com.project.greatcloud13.ClimbingWith.exception.review;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ReviewAccessDeniedException extends BusinessException {
    public ReviewAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED_REVIEW);
    }
}
