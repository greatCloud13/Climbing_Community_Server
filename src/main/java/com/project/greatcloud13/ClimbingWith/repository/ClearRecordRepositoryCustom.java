package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.dto.ClearRecordStatistics;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.User;

public interface ClearRecordRepositoryCustom{

    ClearRecordStatistics getStatistics(User user, Gym gym);


}
