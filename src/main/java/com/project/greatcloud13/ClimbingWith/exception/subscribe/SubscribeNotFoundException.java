package com.project.greatcloud13.ClimbingWith.exception.subscribe;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class SubscribeNotFoundException extends BusinessException {

    public SubscribeNotFoundException() {
        super(ErrorCode.SUBSCRIBE_NOT_FOUND);
    }
}
