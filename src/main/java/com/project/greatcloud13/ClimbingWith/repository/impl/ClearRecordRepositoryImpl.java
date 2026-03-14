package com.project.greatcloud13.ClimbingWith.repository.impl;

import com.project.greatcloud13.ClimbingWith.dto.ClearRecordStatistics;
import com.project.greatcloud13.ClimbingWith.dto.LevelStat;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.QClearRecord;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.ClearRecordRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClearRecordRepositoryImpl implements ClearRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public ClearRecordStatistics getStatistics(User user, Gym gym) {
        QClearRecord clearRecord = QClearRecord.clearRecord;

        List<LevelStat> clearCounts = queryFactory
                .select(Projections.constructor(LevelStat.class,
                        clearRecord.problem.gymLevel,
                        clearRecord.count()))
                .from(clearRecord)
                .where(
                        clearRecord.user.eq(user),
                        clearRecord.gym.eq(gym)
                )
                .groupBy(clearRecord.problem.gymLevel)
                .fetch();

        long totalCount = clearCounts.stream()
                .mapToLong(LevelStat :: getCount)
                .sum();

        ClearRecordStatistics stats = new ClearRecordStatistics();
        stats.setUser(user);
        stats.setGym(gym);
        stats.setProblemCountList(clearCounts);
        stats.setTotalClearCount(totalCount);

        return stats;
    }



}
