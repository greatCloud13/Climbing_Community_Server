package com.project.greatcloud13.ClimbingWith.exception.review;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ProblemReviewNotFoundException extends BusinessException {
    public ProblemReviewNotFoundException() {
        super(ErrorCode.PROBLEM_REVIEW_NOT_FOUND);
    }
}
