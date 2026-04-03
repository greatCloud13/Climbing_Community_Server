package com.project.greatcloud13.ClimbingWith.exception.setting;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class SettingNotFoundException extends BusinessException {
    public SettingNotFoundException() {
        super(ErrorCode.SETTING_NOT_FOUND);
    }
}
