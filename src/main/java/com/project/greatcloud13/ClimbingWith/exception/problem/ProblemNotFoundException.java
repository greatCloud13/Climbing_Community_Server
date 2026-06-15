package com.project.greatcloud13.ClimbingWith.exception.problem;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ProblemNotFoundException extends BusinessException {
    public ProblemNotFoundException() {
        super(ErrorCode.PROBLEM_NOT_FOUND);
    }
}
