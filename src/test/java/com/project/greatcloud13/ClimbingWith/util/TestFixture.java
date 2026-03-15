package com.project.greatcloud13.ClimbingWith.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestFixture {

    /**
     * 특정 객체를 지정된 갯수만큼 생성하여 Page 객체로 반환
     * @param supplier 객체 생성 메서드 (ex: () -> Problem.builder()...build())
     * @param count count 생성할 객체 수
     * @param <T> 객체 타입
     * @return Page<T>
     */
    public static <T> Page<T> createMockPage(Supplier<T> supplier, int count){
        List<T> content = new ArrayList<>();
        for(int i = 0; i<count; i++){
            content.add(supplier.get());
        }
        return new PageImpl<>(content, PageRequest.of(0, count>0 ? count: 1), count);
    }

}
