package com.example.dockercomposepractice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class CacheKey {

    public static final String TEST_KEY = "test";
    public static final int TEST_EXPIRE = 10*10;
}
