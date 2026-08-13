package com.project.greatcloud13.ClimbingWith.repository.impl;

import com.project.greatcloud13.ClimbingWith.dto.GymSearchRequest;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.QGym;
import com.project.greatcloud13.ClimbingWith.repository.GymRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GymRepositoryImpl implements GymRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Gym> search(GymSearchRequest request, Pageable pageable) {
        QGym gym = QGym.gym;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword())) {
            builder.and(gym.gymName.containsIgnoreCase(request.getKeyword())
                    .or(gym.address.containsIgnoreCase(request.getKeyword())));
        }

        if (request.getGymType() != null) {
            builder.and(gym.gymType.eq(request.getGymType()));
        }

        if (StringUtils.hasText(request.getHashtag())) {
            builder.and(gym.hashtags.contains(request.getHashtag()));
        }

        if (request.getIsActive() != null) {
            builder.and(gym.isActive.eq(request.getIsActive()));
        }

        List<Gym> content = queryFactory
                .selectFrom(gym)
                .where(builder)
                .orderBy(gym.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(gym.count())
                .from(gym)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
