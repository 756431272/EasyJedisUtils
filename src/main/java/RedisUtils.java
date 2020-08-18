import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RedisUtils {
    private static Jedis jedis;

    static {
        try {
            Properties properties = new Properties();
            InputStream in = RedisUtils.class.getClassLoader().getResourceAsStream("jedis-config.properties");
            properties.load(in);
            properties.getProperty("maxTotal");
            properties.getProperty("maxWaitMillis");
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle")));
            config.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal")));
            config.setMaxWaitMillis(Integer.parseInt(properties.getProperty("maxWaitMillis")));
            JedisPool pool = new JedisPool(config, "localhost", 6379);
            jedis = pool.getResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取key值
     * @param key
     * @return
     */
    public static String get(String key){
        String value = "";
        try{
            if(jedis.exists(key)){
                value = jedis.get(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return value;
    }

    /**
     * 设置key和value值
     * @param key
     * @param value
     * @param cacheSeconds 超时时间（超过该时间此key失效，0为永不失效）
     */
    public static String set(String key, String value, int cacheSeconds){
        String result = "";
        try{
            result = jedis.set(key, value);
            if(0 != cacheSeconds){
                jedis.expire(key, cacheSeconds);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 删除某个key
     * @param key
     * @return 返回1则为删除成功，返回0则为删除失败
     */
    public static Long delete(String key){
        Long result = 0l;
        try {
            if(jedis.exists(key)){
                result = jedis.del(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 以秒为单位，查询key的剩余生存时间
     * @param key
     * @return key不存在时返回-2
     * key存在但没有剩余生存时间时返回-1
     * 其余数字为正常剩余生存时间
     */
    public static Long ttl(String key){
        Long result = 0l;
        try {
            result = jedis.ttl(key);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 移除某个key的生存时间，使其永不过期
     * @param key
     * @return 返回1则为移除成功，返回0则为移除失败
     */
    public static Long persist(String key){
        Long result = 0l;
        try {
            if(jedis.exists(key)){
                result = jedis.persist(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 查询key的类型
     * @param key
     * @return
     */
    public static String type(String key){
        String type = "";
        try {
            if(jedis.exists(key)){
                type = jedis.type(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return type;
    }

    /**
     * 获取List中的值
     * @param key
     * @return
     */
    public static List<String> getList(String key){
        List<String> result = new ArrayList<String>();
        try {
            if(jedis.exists(key)){
                //从第一个元素开始，一直到最后一个
                result = jedis.lrange(key, 0, -1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 设置类型为List的key和value值
     * @param key
     * @param list
     * @param cacheSeconds
     * @return
     */
    public static Long setList(String key, List<String> list, int cacheSeconds){
        Long result = 0l;
        try {
            result = jedis.rpush(key, (String[]) list.toArray());
            if(0 != cacheSeconds){
                jedis.expire(key, cacheSeconds);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 获取List类型缓存长度
     * @param key
     * @return
     */
    public static Long getListLength(String key){
        Long result = 0l;
        try {
            if (jedis.exists(key)){
                result = jedis.llen(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 通过索引值获取List中的值
     * @param key
     * @param index -1为索引最后一个元素，0位第一个元素，若index不在list的区间范围内，则返回""
     * @return
     */
    public static String getValueFromList(String key,long index){
        String result = "";
        try{
            if(jedis.exists(key)){
                result = jedis.lindex(key, index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 从List里面取第一个数据
     * @param key
     * @return
     */
    public static String getFirstValueFromList(String key){
        String result = "";
        try{
            if(jedis.exists(key)){
                result = jedis.lpop(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 从List里面取最后一个数据
     * @param key
     * @return
     */
    public static String getLastValueFromList(String key) {
        String result = "";
        try{
            if(jedis.exists(key)){
                result = jedis.rpop(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 设置Hash类型的缓存到redis
     * @param key
     * @param valueMap
     * @param cacheSeconds
     * @return
     */
    public static String setHash(String key, Map<String, String> valueMap, int cacheSeconds){
        String result = "";
        try {
            result = jedis.hmset(key, valueMap);
            if(0 != cacheSeconds){
                jedis.expire(key, cacheSeconds);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 对Hash类型中的某个key（field）的value进行更改
     * @param key
     * @param field
     * @param value
     * @return
     */
    public static Long changeHash(String key, String field, String value){
        Long result = 0l;
        try{
            if(jedis.exists(key) && jedis.hexists(key, field)){
                result = jedis.hset(key, field, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 获取Hash类型的数据
     * @param key
     * @return
     */
    public static Map<String , String> getHashAll(String key){
        Map<String, String> result = new HashMap<String, String>();
        try {
            if(jedis.exists(key)){
                result = jedis.hgetAll(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 通过Hash类型的key值和他hash里面的key（这里用field）获取某个value
     * @param key
     * @param field
     * @return
     */
    public static String getHashOne(String key, String field){
        String result = "";
        try {
            if(jedis.exists(key) && jedis.hexists(key, field)){
                result = jedis.hget(key, field);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return  result;
    }

    public static String get
}

