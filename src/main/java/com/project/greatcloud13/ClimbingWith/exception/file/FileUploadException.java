package com.project.greatcloud13.ClimbingWith.exception.file;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.exception.BusinessException;

public class FileUploadException extends BusinessException {
    public FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
