package cn.loftown.wechat.app.code.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新缓存
     */
    public boolean getAndSet(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除缓存
     */
    public boolean delete(final String key) {
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取redisKey
     * @param uniacid
     * @param accountKey 公众号
     * @param key
     * @return
     */
    public String getkey(int uniacid, String accountKey, String key){
        return String.format("ewei_shopv2_syscache_lf_%s_%s_%s", uniacid, accountKey, key);
    }

    public String getGlobalkey(String key){
        return String.format("ewei_shopv2_syscache_lf_global_%s", key);
    }

    public static String getDBCacheKey(int uniacid, String key){
        return "ewei_shop_" + new Md5(uniacid + "_new_" + key).get32();
    }
}
