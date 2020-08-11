import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

}
