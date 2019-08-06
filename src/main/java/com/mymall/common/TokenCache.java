package com.mymall.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
//guava缓存类可以简单理解为自定义的集合map
public class TokenCache {
    //TokenCache的日志对象
  private  static Logger logger= LoggerFactory.getLogger(TokenCache.class);
  //生成<groupId>com.google.guava</groupId>guava缓存
    //guava缓存《key，V》都为string
    //initialCapacity(1000)初始化容量
    //maximumSize(10000)最大容量
    //expireAfterAccess(12, TimeUnit.HOURS)存活时期12 hour
    //他会通过LUR算法来进行淘汰
    private  static LoadingCache<String,String> lodingCache= CacheBuilder.newBuilder().initialCapacity(1000)
          .maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
              @Override
              //默认的数据加载实现当调用getkey时如果可以没有对应的值执行此方法
              public String load(String s) throws Exception {
                  return "null";//"null"防止getkey.equal()出现空指针异常
              }
          });
    public static void setKey(String key,String value){
        lodingCache.put(key,value);
    }
    public static String getKey(String key){
        String value=null;
        try {
            value=lodingCache.get(key);
            if("null".equals(value)){
                return null;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return value;
    }
}
