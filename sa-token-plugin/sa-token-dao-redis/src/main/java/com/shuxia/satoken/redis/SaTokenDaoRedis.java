package com.shuxia.satoken.redis;

import com.shuxia.satoken.dao.SatoKenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *  Sa-Token 持久层实现 [Redis存储、JDK默认序列化]
 * @author shuxia
 * @date 1/18/2023
 */
@Component
public class SaTokenDaoRedis implements SatoKenDao {

    public StringRedisTemplate stringRedisTemplate;

    public RedisTemplate<String,Object> objectRedisTemplate;

    public boolean isInit;

    public SaTokenDaoRedis(){}

    @Autowired
    public void init(RedisConnectionFactory connectionFactory){
        if (!this.isInit) {
            GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
            // 构建StringRedisTemplate
            StringRedisTemplate stringTemplate = new StringRedisTemplate();
            stringTemplate.setConnectionFactory(connectionFactory);
            stringTemplate.afterPropertiesSet();
            // 构建RedisTemplate
            RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(jsonRedisSerializer);
            template.setHashKeySerializer(jsonRedisSerializer);
            template.setValueSerializer(jsonRedisSerializer);
            template.setHashValueSerializer(jsonRedisSerializer);
            template.afterPropertiesSet();

            // 开始初始化相关组件
            this.stringRedisTemplate = stringTemplate;
            this.objectRedisTemplate = template;
            this.isInit = true;
        }
    }


    @Override
    public String get(String key){return stringRedisTemplate.opsForValue().get(key);}

    @Override
    public void set(String key, String value, Long timeout) {
        if (timeout==0 || timeout <=SatoKenDao.NOT_VALUE_EXPIRE){
            return;
        }
        if (timeout ==SatoKenDao.NEVER_EXPIRE){
            stringRedisTemplate.opsForValue().set(key,value);
        }else{
            stringRedisTemplate.opsForValue().set(key,value,timeout, TimeUnit.SECONDS);
        }
    }

    @Override
    public void update(String key,String value){
        long expire = getTimeout(key);
        if (SatoKenDao.NOT_VALUE_EXPIRE ==expire){
            return;
        }
        this.set(key,value,expire);
    }
    @Override
    public void delete(String key){
        stringRedisTemplate.delete(key);
    }

    @Override
    public long getTimeout(String key){
        return stringRedisTemplate.getExpire(key);}

        @Override
    public void updateTimeout(String key,long timeout) {
            if (timeout == SatoKenDao.NEVER_EXPIRE) {
                long expire = getTimeout(key);
                if (expire != SatoKenDao.NEVER_EXPIRE) {
                    this.set(key, this.get(key), timeout);
                }
                return;

            }
            stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }

    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        return objectRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if(timeout == 0 || timeout <= SatoKenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        // 判断是否为永不过期
        if(timeout == SatoKenDao.NEVER_EXPIRE) {
            objectRedisTemplate.opsForValue().set(key, object);
        } else {
            objectRedisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if(expire == SatoKenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        objectRedisTemplate.delete(key);
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        return objectRedisTemplate.getExpire(key);
    }


    @Override
    public void updateObjectTimeout(String key,long timeout){
        if (timeout ==SatoKenDao.NEVER_EXPIRE){
            long expire = getObjectTimeout(key);
            if (expire !=SatoKenDao.NEVER_EXPIRE){
                this.setObject(key,this.getObject(key),timeout);
            }
        }
    }

}
