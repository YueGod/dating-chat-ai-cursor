package com.dating.ai.utils;

/**
 * 用户上下文工具类
 * 使用ThreadLocal在请求处理过程中存储和获取当前用户信息
 *
 * @author dating-ai
 */
public class UserContext {

    /**
     * 用户ID的ThreadLocal存储
     */
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     */
    public static String getUserId() {
        return USER_ID.get();
    }

    /**
     * 清除当前线程的用户信息
     * 在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        USER_ID.remove();
    }
}