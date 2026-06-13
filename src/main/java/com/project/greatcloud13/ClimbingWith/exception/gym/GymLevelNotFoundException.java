package com.project.greatcloud13.ClimbingWith.exception.gym;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class GymLevelNotFoundException extends BusinessException {
    public GymLevelNotFoundException() {
        super(ErrorCode.GYM_LEVEL_NOT_FOUND);
    }
}
