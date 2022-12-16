package com.shuxia.satoken.dao;

import com.shuxia.satoken.session.SaSession;

/**
 * @author shuxia
 * @date 11/27/2022
 */
public interface SatoKenDao {
    /** 常量，表示一个key永不过期 (在一个key被标注为永远不过期时返回此值) */
    public static final long NEVER_EXPIRE = -1;

    /** 常量，表示系统中不存在这个缓存 (在对不存在的key获取剩余存活时间时返回此值) */
    public static final long NOT_VALUE_EXPIRE = -2;


    /**
     * 获取session，无返回空
     * @param sessionId
     * @return
     */
    default SaSession getSession(String sessionId){
        return (SaSession)getObject(sessionId);
    }
    /**
     * 获取Object，如无返空
     * @param key 键名称
     * @return object
     */
    Object getObject(String key);

   default void setSession(SaSession session, long timeout){
      setObject(session.getId(),session,timeout);
   }

    void setObject(String key, Object object, long timeout);

    default long getSessionTimeout(String id){
        return getObjectTimeout(id);
    }

    long getObjectTimeout(String id);

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     * @param sessionId 指定key
     * @param minTimeout 过期时间
     */
    default void updateSessionTimeout(Long minTimeout,String sessionId){
        updateObjectSessionTimeout(sessionId,minTimeout);
    }

    void updateObjectSessionTimeout(String sessionId, Long minTimeout);

    /**
     * 更新session
     * @param saSession
     */
    default void updateSession(SaSession saSession){
        updateObject(saSession.getId(),saSession);
    }

    void updateObject(String id, SaSession saSession);

    /**
     * 写入value，设置存活时间
     * @param splicingKeyToken
     * @param valueOf
     * @param timeout
     */
    void set(String splicingKeyToken, String valueOf, Long timeout);
    /**
     * 删除key
     * @param splicingKeyToken
     */
    void delete(String splicingKeyToken);

    /**
     * 注销会话
     * @param id
     */
    default void deleteSession(String id){
        deleteObject(id);
    }

    void deleteObject(String id);

    /**
     * 更新value
     * @param key
     * @param value
     */
    void update(String key, String value);

    String get(String key);

    long getTimeout(String key);
}
