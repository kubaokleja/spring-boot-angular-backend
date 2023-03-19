package com.kubaokleja.springbootangular.auth;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    private final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptService.class);
    private static final int MAXIMUM_ATTEMPT_NUMBER = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<String, Integer> loggingAttemptCache;

    public LoginAttemptService(){
        super();
        loggingAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES).maximumSize(100)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    void evictUserFromLoginAttemptCache(String username){
        loggingAttemptCache.invalidate(username) ;
    }

    void addUserToLoginAttemptCache(String username) {
        int attempt = 0;
        try {
            attempt = ATTEMPT_INCREMENT + loggingAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loggingAttemptCache.put(username, attempt);
    }

    boolean hasExceededMaxAttempts(String username) {
        try {
            return loggingAttemptCache.get(username) >= MAXIMUM_ATTEMPT_NUMBER;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }
}
