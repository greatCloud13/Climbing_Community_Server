package com.project.greatcloud13.ClimbingWith.exception.gym;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class GymAccessDeniedException extends BusinessException {
    public GymAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED_GYM);
    }
}
