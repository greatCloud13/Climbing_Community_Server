package com.project.greatcloud13.ClimbingWith.exception.clearrecord;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class ClearRecordNotFoundException extends BusinessException {
    public ClearRecordNotFoundException() {
        super(ErrorCode.CLEAR_RECORD_NOT_FOUND);
    }
}
