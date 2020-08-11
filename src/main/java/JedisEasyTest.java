import redis.clients.jedis.Jedis;

public class JedisEasyTest {

    public static void main(String[] args){
        Jedis jedis = new Jedis("localhost", 6379, 100000);
        try {
            // 修改某个key值
            jedis.set("jedisEasyTest", "test");
            for(int i = 0; i < 1000; i ++){
                // 在列表中新增List列表值
                jedis.lpush("jedisEasyList", "test:" + i);
            }
        }finally {
            jedis.close();
        }
    }
}
