package com.shuxia.satoken.dao;

import com.shuxia.satoken.session.SaSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shuxia
 * @date 11/30/2022
 */
public class SaTokenDaoDefaultImpl implements SatoKenDao{
    /**
     * 数据集合
     */
    public Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>();

    /**
     * 过期时间集合 (单位: 毫秒) , 记录所有key的到期时间 [注意不是剩余存活时间]
     */
    public Map<String, Long> expireMap = new ConcurrentHashMap<String, Long>();

    public SaTokenDaoDefaultImpl(){
        //TODO 初始化定时任务
    }

    @Override
    public Object getObject(String key) {
        clearKeyByTimeout(key);
        return dataMap.get(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout ==0 || timeout == SatoKenDao.NOT_VALUE_EXPIRE){
      return;
        }
        dataMap.put(key,object);
        expireMap.put(key,(timeout == SatoKenDao.NEVER_EXPIRE)?(SatoKenDao.NEVER_EXPIRE):System.currentTimeMillis()+timeout *1000);
    }

    @Override
    public long getObjectTimeout(String key) {
        return getKeyTimeout(key);
    }

    @Override
    public void updateObjectSessionTimeout(String sessionId, Long minTimeout) {
        expireMap.put(sessionId,(minTimeout ==SatoKenDao.NEVER_EXPIRE)?SatoKenDao.NEVER_EXPIRE: (System.currentTimeMillis() + minTimeout * 1000));
    }

    @Override
    public void updateObject(String id, SaSession saSession) {
        if (getKeyTimeout(id) ==SatoKenDao.NOT_VALUE_EXPIRE){
            return;
        }
        dataMap.put(id,saSession);
    }

    @Override
    public void set(String key, String value, Long timeout) {
        if(timeout == 0 || timeout <= SatoKenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        dataMap.put(key,value);
        expireMap.put(key, (timeout == SatoKenDao.NEVER_EXPIRE) ? (SatoKenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));

    }

    @Override
    public void delete(String key) {
        remove(key);
    }

    @Override
    public void deleteObject(String id) {
       remove(id);
    }

    /**
     * 获取指定key的剩余存活时间 (单位：秒)
     */
     long getKeyTimeout(String key) {
         //检查是否过期
         clearKeyByTimeout(key);
         //获取过期时间
         Long expirationTimeout = expireMap.get(key);
         //判断
         if (expirationTimeout ==null){
             return SatoKenDao.NOT_VALUE_EXPIRE;
         }
         if (expirationTimeout ==SatoKenDao.NEVER_EXPIRE){
             return SatoKenDao.NEVER_EXPIRE;
         }
         //剩余时间
         long timeout = expirationTimeout - System.currentTimeMillis() * 1000;
         //清除
         if (timeout <0){
             remove(key);
             return SatoKenDao.NOT_VALUE_EXPIRE;
         }
         return timeout;

    }

    /**
     * 清除过期key
     * @param key
     */
    void clearKeyByTimeout(String key){
        Long expirationTime = expireMap.get(key);
        //清除条件；不为空 && 有期限 && 已过期
        if (expirationTime !=null && expirationTime != SatoKenDao.NEVER_EXPIRE && expirationTime < System.currentTimeMillis()){
           remove(key);
        }
    }

    /**
     * 封装 删除
     * @param key
     */
    public void remove(String key){
        dataMap.remove(key);
        expireMap.remove(key);
    }


}
