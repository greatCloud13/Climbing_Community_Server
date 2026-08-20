package com.project.greatcloud13.ClimbingWith.exception.gym;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class GymImageLimitExceededException extends BusinessException {
    public GymImageLimitExceededException() {
        super(ErrorCode.GYM_IMAGE_LIMIT_EXCEEDED);
    }
}
