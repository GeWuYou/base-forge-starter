package com.gewuyou.baseforge.redis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.redis.exception.CacheException;
import com.gewuyou.baseforge.redis.i18n.enums.CacheResponseInformation;
import com.gewuyou.baseforge.redis.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现类
 *
 * @author gewuyou
 * @since 2024-10-03 14:44:44
 */
@Service
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheServiceImpl(
            ObjectMapper objectMapper,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 清理顶级命名空间下的所有缓存
     * @param topNamespace 顶级命名空间
     */
    @Override
    public void clearTopNamespace(String topNamespace) {
        String pattern = topNamespace + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 清理特定命名空间下的所有缓存
     * @param topNamespace 顶级命名空间
     * @param namespace 子命名空间
     */
    @Override
    public void clearNamespace(String topNamespace, String namespace) {
        String pattern = topNamespace + ":" + namespace + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除特定的缓存项
     * @param topNamespace 顶级命名空间
     * @param namespace 子命名空间
     * @param key 键
     */
    @Override
    public void deleteCache(String topNamespace, String namespace, String key) {
        String redisKey = topNamespace + ":" + namespace + ":" + key;
        redisTemplate.delete(redisKey);
    }
    /**
     * 设置字符串
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置字符串
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @apiNote 时间单位：秒
     */
    @Override
    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置对象
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @apiNote 此处的值为对象使用了Json序列化，如果在对象中具有复杂结构时使用
     */
    @Override
    public void setObject(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, timeout, unit);
        } catch (JsonProcessingException e) {
            throw new CacheException(CacheResponseInformation.JSON_SERIALIZE_ERROR.getResponseI8nMessageCode());
        }
    }

    /**
     * 设置对象
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @apiNote 时间单位：秒
     */
    @Override
    public void setObject(String key, Object value, long timeout) {
        setObject(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取对象
     *
     * @param key   键
     * @param clazz 对象类型
     * @return 对象
     */
    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        String jsonValue = (String) get(key);
        if (jsonValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonValue, clazz);
        } catch (JsonProcessingException e) {
            throw new CacheException("JSON反序列化失败",CacheResponseInformation.JSON_DESERIALIZE_ERROR);
        }
    }

    /**
     * 获取对象
     *
     * @param key           键
     * @param typeReference 类型引用
     * @return 对象
     */
    @Override
    public <T> T getObject(String key, TypeReference<T> typeReference) {
        String jsonValue = (String) get(key);
        if (jsonValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonValue, typeReference);
        } catch (JsonProcessingException e) {
            throw new CacheException("JSON反序列化失败",CacheResponseInformation.JSON_DESERIALIZE_ERROR);
        }
    }


    /**
     * 删除值
     *
     * @param key 键
     * @return 是否成功
     */
    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean delayedDelete(String key, long delay, TimeUnit unit) {
        return redisTemplate.expire(key, delay, unit);
    }


    /**
     * 批量删除值
     *
     * @param keys 键列表
     * @return 成功删除的个数
     */
    @Override
    public Long delete(String... keys) {
        return redisTemplate.delete(List.of(keys));
    }

    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public Boolean expire(String key, long time, TimeUnit unit) {
        return redisTemplate.expire(key, time, unit);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long incrExpire(String key, long time) {
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count != null && count == 1) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return count;
    }

    /**
     * 自增指定键的值，并设置过期时间
     *
     * @param key  键
     * @param time 过期时间
     * @param unit 时间单位
     * @return 增量后的结果
     */
    @Override
    public Long incrExpire(String key, long time, TimeUnit unit) {
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count != null && count == 1) {
            redisTemplate.expire(key, time, unit);
        }
        return count;
    }

    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 设置哈希表中指定字段的值
     *
     * @param key     键
     * @param hashKey 哈希表字段
     * @param value   字段的值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否成功
     */
    @Override
    public Boolean hSet(String key, String hashKey, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, timeout, unit);
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }

    @Override
    public void hSetAll(String key, Map<String, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public Long hDel(String key, Object... hashKey) {
       return redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    @Override
    public Double zIncr(String key, Object value, Double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    @Override
    public Double zDecr(String key, Object value, Double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, -score);
    }

    @Override
    public Map<Object, Double> zReverseRangeWithScore(String key, long start, long end) {
        Map<Object, Double> map = new HashMap<>();
        Objects.requireNonNull(
                        redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end))
                .stream()
                .filter(
                        objectTypedTuple -> map.put(objectTypedTuple.getValue(), objectTypedTuple.getScore()) != null)
                .forEach(objectTypedTuple -> {
                    throw new IllegalStateException("Duplicate key");
                });
        return map;
    }

    @Override
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    @Override
    public Map<Object, Double> zAllScore(String key) {
        Map<Object, Double> map = new HashMap<>();
        for (ZSetOperations.TypedTuple<Object> objectTypedTuple : Objects.requireNonNull(redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1))) {
            map.putIfAbsent(objectTypedTuple.getValue(), objectTypedTuple.getScore());
        }
        return map;
    }

    @Override
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long sAddExpire(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }

    /**
     * 判断集合中是否存在指定键值
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    @Override
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 获取集合的差集
     *
     * @param keys 键列表
     * @return 差集
     */
    @Override
    public Set<Object> sDiff(String... keys) {
        return redisTemplate.opsForSet().difference(List.of(keys));
    }

    @Override
    public Set<Object> sInter(String... keys) {
        return redisTemplate.opsForSet().intersect(List.of(keys));
    }

    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long lPush(String key, Object value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }

    @Override
    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Long lPushAll(String key, Long time, Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public Boolean bitAdd(String key, int offset, boolean b) {
        return redisTemplate.opsForValue().setBit(key, offset, b);
    }

    @Override
    public Boolean bitGet(String key, int offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    @Override
    public Long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) con -> con.stringCommands().bitCount(key.getBytes()));
    }

    @Override
    public List<Long> bitField(String key, int limit, int offset) {
        return redisTemplate.execute((RedisCallback<List<Long>>) con ->
                con.stringCommands().bitField(key.getBytes(),
                        BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(limit)).valueAt(offset)));
    }

    @Override
    public byte[] bitGetAll(String key) {
        return redisTemplate.execute((RedisCallback<byte[]>) con -> con.stringCommands().get(key.getBytes()));
    }

    @Override
    public Long hyperAdd(String key, Object... value) {
        return redisTemplate.opsForHyperLogLog().add(key, value);
    }

    @Override
    public Long hyperGet(String... key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }

    @Override
    public void hyperDel(String key) {
        redisTemplate.opsForHyperLogLog().delete(key);
    }

    @Override
    public Long geoAdd(String key, Double x, Double y, String name) {
        return redisTemplate.opsForGeo().add(key, new Point(x, y), name);
    }

    @Override
    public List<Point> geoGetPointList(String key, Object... place) {
        return redisTemplate.opsForGeo().position(key, place);
    }

    @Override
    public Distance geoCalculationDistance(String key, String placeOne, String placeTow) {
        return redisTemplate.opsForGeo()
                .distance(key, placeOne, placeTow, RedisGeoCommands.DistanceUnit.KILOMETERS);
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoNearByPlace(String key, String place, Distance distance, long limit, Sort.Direction sort) {
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates();
        // 判断排序方式
        if (Sort.Direction.ASC == sort) {
            args.sortAscending();
        } else {
            args.sortDescending();
        }
        args.limit(limit);
        return redisTemplate.opsForGeo()
                .radius(key, place, distance, args);
    }

    @Override
    public List<String> geoGetHash(String key, String... place) {
        Object[] objects = Arrays.stream(place).toArray();
        return redisTemplate.opsForGeo()
                .hash(key, objects);
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, Duration duration) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, duration);
    }
}
