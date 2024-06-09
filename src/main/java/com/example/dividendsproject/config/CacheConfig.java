package com.example.dividendsproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {
    //host와 port를 가져와준다
    @Value("${spring.redis.host}")//옵션 경로를 입력해 값을 가져올 수 있도록 한다.
    private String host;
    @Value("${spring.redis.port}")
    private int port; //서비스가 초기화되는 과정에서 그 값들이 각각 host와 port변수에 맵핑됨

    //캐쉬에 적용시켜 사용하기 위해서는 캐쉬매니저bean을 추가로 생성해줘야함
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        //redis는 자바 시스템 외부에 있는 캐시 서버이기 때문에, 그곳에 저장하기 위해서는 저장할 데이터를 직열화(serialization) 해줘야함
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()//serialization을 지정해줘야함
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
                //.entryTtl(Duration.of()); 이렇게하면 redis 전체에 설정(데이터의 유효기간 TTL

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)//redis와 connection을 맺은 설정정보를 넣어줌
                .cacheDefaults(conf)
                .build();//빌더 패턴을 사용하여 CacheManager를 초기화해줌
    }

    //redis서버와 연결..connection을 관리하기 위한 redis connection 팩토리 bean을 초기화
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        //RedisClusterConnection -> cluster 방법
        //싱글 인스턴스 서버여서 RedisStandaloneConfiguration 생성
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(this.host);
        conf.setPort(this.port);
        return new LettuceConnectionFactory(conf);//이걸로 redisConnectionFactory의 빈을 사용하게 됨
    }
}
//캐시 데이터 삭제해야하는 이유
//캐시 데이터가 계속 있으면 해당 키를 가진 데이터가 업데이트 되었는데, 캐시에 데이터 남아있으면 실제 클라이언트 요청은 뒤에있는 서버까지 가지 않고 캐시에 있는 업데이트 되지 않은 데이터를 내려줌
//데이터가 업데이트 되는 경우에는 해당 캐시 데이터를 비워주거나 캐시의 데이터도 같이 업데이트 해줘야함

//캐시의 데이터가 계속 저장이 되면 공간에 한계가 있어서 오래 저장되었거나 쓰이지 않는 데이터가 있으면 캐시에서 비워줄 수 있음