package com.example.demo.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * Created by WCL on 2018/7/31.
 */
public class JedisClusterUtils {
    private static Logger logger = LoggerFactory.getLogger(JedisClusterUtils.class);
    private static JedisCluster jedisCluster = (JedisCluster)SpringContextUtil.getBean(JedisCluster.class);
    private static final String CHARSET_NAME = "UTF-8";
    private static final int EXPIRE = 60;
    private static final String LOCK_KEY_PREFIX = "_REDIS_LOCK_";

    public JedisClusterUtils() {
    }

    public static String get(String key) {
        String value = null;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    value = jedisCluster.get(key);
                    value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value)?value:null;
                }
            } catch (Exception var6) {
                logger.warn("get {} = {}", new Object[]{key, value, var6});
            }

            return value;
        } finally {
            ;
        }
    }

    public static byte[] get(byte[] key) {
        byte[] value = null;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    value = jedisCluster.get(key);
                }
            } catch (Exception var6) {
                logger.warn("get {} = {}", new Object[]{key, value, var6});
            }

            return value;
        } finally {
            ;
        }
    }

    public static Object getObject(Object key) {
        Object value = null;

        try {
            try {
                if(jedisCluster.exists(getBytesKey(key)).booleanValue()) {
                    value = toObject(jedisCluster.get(getBytesKey(key)));
                }
            } catch (Exception var6) {
                logger.warn("getObject {} = {}", new Object[]{key, value, var6});
            }

            return value;
        } finally {
            ;
        }
    }

    public static String set(String key, String value, int cacheSeconds) {
        String result = null;

        try {
            try {
                result = jedisCluster.set(key, value);
                if(cacheSeconds != 0) {
                    jedisCluster.expire(key, cacheSeconds);
                }
            } catch (Exception var8) {
                logger.warn("set {} = {}", new Object[]{key, value, var8});
            }

            return result;
        } finally {
            ;
        }
    }

    public static String setObject(Object key, Object value, int cacheSeconds) {
        String result = null;

        try {
            try {
                byte[] e = getBytesKey(key);
                result = jedisCluster.set(e, toBytes(value));
                if(cacheSeconds != 0) {
                    jedisCluster.expire(e, cacheSeconds);
                }

                if(cacheSeconds != 0) {
                    jedisCluster.expire(e, cacheSeconds);
                }
            } catch (Exception var8) {
                logger.warn("setObject {} = {}", new Object[]{key, value, var8});
            }

            return result;
        } finally {
            ;
        }
    }

    public static List<String> getList(String key) {
        List value = null;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    value = jedisCluster.lrange(key, 0L, -1L);
                }
            } catch (Exception var6) {
                logger.warn("getList {} = {}", new Object[]{key, value, var6});
            }

            return value;
        } finally {
            ;
        }
    }

    public static List getObjectList(Object key) {
        ArrayList value = null;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    List list = jedisCluster.lrange(e, 0L, -1L);
                    value = Lists.newArrayList();
                    Iterator var4 = list.iterator();

                    while(var4.hasNext()) {
                        byte[] bs = (byte[])var4.next();
                        value.add(toObject(bs));
                    }
                }
            } catch (Exception var9) {
                logger.warn("getObjectList {} = {}", new Object[]{key, value, var9});
            }

            return value;
        } finally {
            ;
        }
    }

    public static long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0L;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    jedisCluster.del(key);
                }

                result = jedisCluster.rpush(key, (String[])((String[])value.toArray())).longValue();
                if(cacheSeconds != 0) {
                    jedisCluster.expire(key, cacheSeconds);
                }
            } catch (Exception var9) {
                logger.warn("setList {} = {}", new Object[]{key, value, var9});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long setObjectList(Object key, List value, int cacheSeconds) {
        long result = 0L;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    jedisCluster.del(e);
                }

                ArrayList list = Lists.newArrayList();
                Iterator blists = value.iterator();

                while(blists.hasNext()) {
                    Object o = blists.next();
                    list.add(toBytes(o));
                }

                byte[][] blists1 = new byte[list.size()][];
                list.toArray(blists1);
                result = jedisCluster.rpush(e, blists1).longValue();
                if(cacheSeconds != 0) {
                    jedisCluster.expire(e, cacheSeconds);
                }
            } catch (Exception var12) {
                logger.warn("setObjectList {} = {}", new Object[]{key, value, var12});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long listAdd(String key, String... value) {
        long result = 0L;

        try {
            try {
                result = jedisCluster.rpush(key, value).longValue();
            } catch (Exception var8) {
                logger.warn("listAdd {} = {}", new Object[]{key, value, var8});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long listObjectAdd(Object key, Object... value) {
        long result = 0L;

        try {
            try {
                byte[] e = getBytesKey(key);
                ArrayList list = Lists.newArrayList();
                Object[] blists = value;
                int var7 = value.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Object o = blists[var8];
                    list.add(toBytes(o));
                }

                byte[][] var15 = new byte[list.size()][];
                list.toArray(var15);
                result = jedisCluster.rpush(e, var15).longValue();
            } catch (Exception var13) {
                logger.warn("listObjectAdd {} = {}", new Object[]{key, value, var13});
            }

            return result;
        } finally {
            ;
        }
    }

    public static Set<String> getSet(String key) {
        Set value = null;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    value = jedisCluster.smembers(key);
                }
            } catch (Exception var6) {
                logger.warn("getSet {} = {}", new Object[]{key, value, var6});
            }

            return value;
        } finally {
            ;
        }
    }

    public static Set getObjectSet(Object key) {
        HashSet value = null;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    value = Sets.newHashSet();
                    Set set = jedisCluster.smembers(e);
                    Iterator var4 = set.iterator();

                    while(var4.hasNext()) {
                        byte[] bs = (byte[])var4.next();
                        value.add(toObject(bs));
                    }
                }
            } catch (Exception var9) {
                logger.warn("getObjectSet {} = {}", new Object[]{key, value, var9});
            }

            return value;
        } finally {
            ;
        }
    }

    public static long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0L;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    jedisCluster.del(key);
                }

                result = jedisCluster.sadd(key, (String[])((String[])value.toArray())).longValue();
                if(cacheSeconds != 0) {
                    jedisCluster.expire(key, cacheSeconds);
                }
            } catch (Exception var9) {
                logger.warn("setSet {} = {}", new Object[]{key, value, var9});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long setObjectSet(Object key, Set value, int cacheSeconds) {
        long result = 0L;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    jedisCluster.del(e);
                }

                HashSet set = Sets.newHashSet();
                Iterator bsets = value.iterator();

                while(bsets.hasNext()) {
                    Object o = bsets.next();
                    set.add(toBytes(o));
                }

                byte[][] bsets1 = new byte[set.size()][];
                set.toArray(bsets1);
                result = jedisCluster.sadd(e, bsets1).longValue();
                if(cacheSeconds != 0) {
                    jedisCluster.expire(e, cacheSeconds);
                }
            } catch (Exception var12) {
                logger.warn("setObjectSet {} = {}", new Object[]{key, value, var12});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long setSetAdd(String key, String... value) {
        long result = 0L;

        try {
            try {
                result = jedisCluster.sadd(key, value).longValue();
                logger.debug("setSetAdd {} = {}", key, value);
            } catch (Exception var8) {
                logger.warn("setSetAdd {} = {}", new Object[]{key, value, var8});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long setSetObjectAdd(Object key, Object... value) {
        long result = 0L;

        try {
            try {
                byte[] e = getBytesKey(key);
                HashSet set = Sets.newHashSet();
                Object[] bsets = value;
                int var7 = value.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Object o = bsets[var8];
                    set.add(toBytes(o));
                }

                byte[][] var15 = new byte[set.size()][];
                set.toArray(var15);
                result = jedisCluster.rpush(e, var15).longValue();
            } catch (Exception var13) {
                logger.warn("setSetObjectAdd {} = {}", new Object[]{key, value, var13});
            }

            return result;
        } finally {
            ;
        }
    }

    public static Map<String, String> getMap(String key) {
        Map value = null;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    value = jedisCluster.hgetAll(key);
                }
            } catch (Exception var6) {
                logger.warn("getMap {} = {}", new Object[]{key, value, var6});
            }

            return value;
        } finally {
            ;
        }
    }

    public static Map<Object, Object> getObjectMap(Object key) {
        HashMap value = null;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    value = Maps.newHashMap();
                    Map map = jedisCluster.hgetAll(e);
                    Iterator var4 = map.entrySet().iterator();

                    while(var4.hasNext()) {
                        Map.Entry e1 = (Map.Entry)var4.next();
                        value.put(toObject((byte[])e1.getKey()), toObject((byte[])e1.getValue()));
                    }
                }
            } catch (Exception var9) {
                logger.warn("getObjectMap {} = {}", new Object[]{key, value, var9});
            }

            return value;
        } finally {
            ;
        }
    }

    public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    jedisCluster.del(key);
                }

                result = jedisCluster.hmset(key, value);
                if(cacheSeconds != 0) {
                    jedisCluster.expire(key, cacheSeconds);
                }
            } catch (Exception var8) {
                logger.warn("setMap {} = {}", new Object[]{key, value, var8});
            }

            return result;
        } finally {

        }
    }

    public static String setObjectMap(String key, Map<Object, Object> value, int cacheSeconds) {
        String result = null;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    jedisCluster.del(key);
                }

                HashMap map = Maps.newHashMap();
                Iterator var6 = value.entrySet().iterator();

                while(var6.hasNext()) {
                    Map.Entry e1 = (Map.Entry)var6.next();
                    map.put(toBytes(e1.getKey()), toBytes(e1.getValue()));
                }

                result = jedisCluster.hmset(e, map);
                if(cacheSeconds != 0) {
                    jedisCluster.expire(e, cacheSeconds);
                }
            } catch (Exception var11) {
                logger.warn("setObjectMap {} = {}", new Object[]{key, value, var11});
            }

            return result;
        } finally {
            ;
        }
    }

    public static String mapPut(String key, Map<String, String> value) {
        String result = null;

        try {
            try {
                result = jedisCluster.hmset(key, value);
            } catch (Exception var7) {
                logger.warn("mapPut {} = {}", new Object[]{key, value, var7});
            }

            return result;
        } finally {

        }
    }

    public static String mapObjectPut(Object key, Map<Object, Object> value) {
        String result = null;

        try {
            try {
                byte[] e = getBytesKey(key);
                HashMap map = Maps.newHashMap();
                Iterator var5 = value.entrySet().iterator();

                while(var5.hasNext()) {
                    Map.Entry e1 = (Map.Entry)var5.next();
                    map.put(toBytes(e1.getKey()), toBytes(e1.getValue()));
                }

                result = jedisCluster.hmset(e, map);
                logger.debug("mapObjectPut {} = {}", key, value);
            } catch (Exception var10) {
                logger.warn("mapObjectPut {} = {}", new Object[]{key, value, var10});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long mapRemove(String key, String mapKey) {
        long result = 0L;

        try {
            try {
                result = jedisCluster.hdel(key, new String[]{mapKey}).longValue();
            } catch (Exception var8) {
                logger.warn("mapRemove {}  {}", new Object[]{key, mapKey, var8});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long mapObjectRemove(Object key, Object mapKey) {
        long result = 0L;

        try {
            try {
                byte[] e = getBytesKey(key);
                result = jedisCluster.hdel(e, new byte[][]{toBytes(mapKey)}).longValue();
            } catch (Exception var8) {
                logger.warn("mapObjectRemove {}  {}", new Object[]{key, mapKey, var8});
            }

            return result;
        } finally {
            ;
        }
    }

    public static boolean mapExists(String key, String mapKey) {
        boolean result = false;

        try {
            try {
                result = jedisCluster.hexists(key, mapKey).booleanValue();
            } catch (Exception var7) {
                logger.warn("mapExists {}  {}", new Object[]{key, mapKey, var7});
            }

            return result;
        } finally {
            ;
        }
    }

    public static boolean mapObjectExists(Object key, Object mapKey) {
        boolean result = false;

        try {
            try {
                byte[] e = getBytesKey(key);
                result = jedisCluster.hexists(e, toBytes(mapKey)).booleanValue();
            } catch (Exception var7) {
                logger.warn("mapObjectExists {}  {}", new Object[]{key, mapKey, var7});
            }

            return result;
        } finally {
            ;
        }
    }

    public static long del(String key) {
        long result = 0L;

        try {
            try {
                if(jedisCluster.exists(key).booleanValue()) {
                    result = jedisCluster.del(key).longValue();
                } else {
                    logger.debug("del {} not exists", key);
                }
            } catch (Exception var7) {
                logger.warn("del {}", key, var7);
            }

            return result;
        } finally {
            ;
        }
    }

    public static long delObject(Object key) {
        long result = 0L;

        try {
            try {
                byte[] e = getBytesKey(key);
                if(jedisCluster.exists(e).booleanValue()) {
                    result = jedisCluster.del(e).longValue();
                } else {
                    logger.debug("delObject {} not exists", key);
                }
            } catch (Exception var7) {
                logger.warn("delObject {}", key, var7);
            }

            return result;
        } finally {
            ;
        }
    }

    public static boolean exists(String key) {
        boolean result = false;

        try {
            try {
                result = jedisCluster.exists(key).booleanValue();
            } catch (Exception var6) {
                logger.warn("exists {}", key, var6);
            }

            return result;
        } finally {
            ;
        }
    }

    public static boolean existsObject(Object key) {
        boolean result = false;

        try {
            try {
                byte[] e = getBytesKey(key);
                result = jedisCluster.exists(e).booleanValue();
            } catch (Exception var6) {
                logger.warn("existsObject {}", key, var6);
            }

            return result;
        } finally {
            ;
        }
    }

    public static byte[] getBytesKey(Object object) {
        return object instanceof String?StringUtils.getBytes((String)object):ObjectUtils.serialize(object);
    }

    public static byte[] toBytes(Object object) {
        return ObjectUtils.serialize(object);
    }

    public static Object toObject(byte[] bytes) {
        return ObjectUtils.unserialize(bytes);
    }

    public static List<String> keys(String pattern) {
        ArrayList keys = Lists.newArrayList();
        Map clusterNodes = jedisCluster.getClusterNodes();
        Iterator var3 = clusterNodes.keySet().iterator();

        while(var3.hasNext()) {
            String k = (String)var3.next();
            JedisPool jp = (JedisPool)clusterNodes.get(k);
            Jedis connection = jp.getResource();

            try {
                keys.addAll(connection.keys(pattern));
            } catch (Exception var11) {
                logger.error("Getting keys error: {}", var11);
            } finally {
                logger.debug("Connection closed.");
                connection.close();
            }
        }

        logger.debug("Keys gotten!");
        return keys;
    }

    public static boolean tryLock(String key, Integer expire) {
        String rediskey = "_REDIS_LOCK_" + key;
        boolean locked = false;

        try {
            if(expire == null || expire.intValue() == 0) {
                expire = Integer.valueOf(60);
            }

            Long e = jedisCluster.setnx(rediskey, rediskey);
            if(e.longValue() == 1L) {
                jedisCluster.expire(rediskey, expire.intValue());
                locked = true;
            } else {
                locked = false;
            }
        } catch (Exception var5) {
            logger.error(var5.getMessage(), var5);
        }

        return locked;
    }

    public static void unLock(String key) {
        String rediskey = "_REDIS_LOCK_" + key;
        jedisCluster.del(rediskey);
    }

    public static Long mapIncrBy(String key, String field, long value) {
        Long result = null;

        try {
            try {
                result = jedisCluster.hincrBy(key, field, value);
            } catch (Exception var9) {
                logger.warn("mapIncrBy {}", key, var9);
            }

            return result;
        } finally {
            ;
        }
    }
}
