package com.project.greatcloud13.ClimbingWith.exception.problemtrylog;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ProblemTryLogAccessDeniedException extends BusinessException {
    public ProblemTryLogAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED_PROBLEM_TRY_LOG);
    }
}
