package com.project.greatcloud13.ClimbingWith.exception.gym;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class GymNotFoundException extends BusinessException {
    public GymNotFoundException() {
        super(ErrorCode.GYM_NOT_FOUND);
    }
}
