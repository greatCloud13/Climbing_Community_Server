package com.project.greatcloud13.ClimbingWith.exception.common;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class NotImplementedException extends BusinessException {
    public NotImplementedException() {
        super(ErrorCode.NOT_IMPLEMENTED);
    }
}
