package com.project.greatcloud13.ClimbingWith.exception.clearrecord;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ClearRecordAccessDeniedException extends BusinessException {
    public ClearRecordAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED_CLEAR_RECORD);
    }
}
