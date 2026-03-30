package com.project.greatcloud13.ClimbingWith.exception.sector;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class SectorNotFoundException extends BusinessException {
    public SectorNotFoundException(){
        super(ErrorCode.SECTOR_NOT_FOUND);
    }
}
