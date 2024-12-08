package com.paul.knowledgeHub.constant;

public interface RedisConstant {
    /**
     * 用户签到记录的Redis前缀
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins";

    /**
     * 获取用户签到记录的Redis Key
     * @param year
     * @param userId
     * @return
     */
    static String getUserSignInRedisKeyPrefix(int year, long userId){
        return String.format("%s:%s:%s", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);
    }
}
