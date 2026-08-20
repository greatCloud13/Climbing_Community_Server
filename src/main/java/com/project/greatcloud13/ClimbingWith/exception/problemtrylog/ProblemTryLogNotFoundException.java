package com.project.greatcloud13.ClimbingWith.exception.problemtrylog;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ProblemTryLogNotFoundException extends BusinessException {
    public ProblemTryLogNotFoundException() {
        super(ErrorCode.PROBLEM_TRY_LOG_NOT_FOUND);
    }
}
