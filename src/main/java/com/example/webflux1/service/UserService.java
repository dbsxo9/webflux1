package com.example.webflux1.service;

import com.example.webflux1.dto.UserResponse;
import com.example.webflux1.repository.User;
import com.example.webflux1.repository.UserR2dbcRepository;
import com.example.webflux1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {
    //private final UserRepository userRepository;
    private final UserR2dbcRepository userR2dbcRepository;

    private final ReactiveRedisTemplate<String, User> reactiveRedisTemplate;

    public Mono<User> create(String name, String email){
        //return userRepository.save(User.builder().name(name).email(email).build());
        return userR2dbcRepository.save(User.builder().name(name).email(email).build());
    }

    public Flux<User> findAll(){
        //return userRepository.findAll();
        return userR2dbcRepository.findAll();
    }

    private String getUserCacheKey(Long id){
        return "users:%d".formatted(id);
    }

    public Mono<User> findById(Long id){
        //return userRepository.findById(id);
//        return userR2dbcRepository.findById(id);

        // 1. redis 조회
        // 2. 값이 존재하면 응답
        // 3. 없으면 DB에 질의하고 그 결과를 redis에 저장하는 흐름
        return reactiveRedisTemplate.opsForValue()
                .get("users:%d".formatted(id))
                .switchIfEmpty(
                        userR2dbcRepository.findById(id)
                                .flatMap(u -> reactiveRedisTemplate.opsForValue()
                                        .set("users%d".formatted(id), u, Duration.ofSeconds(30))
                                        .then(Mono.just(u)))
                );
    }

    public Mono<Void> deleteById(Long id){
        //return userRepository.deleteById(id);
        return userR2dbcRepository.deleteById(id)
                .then(reactiveRedisTemplate.unlink(getUserCacheKey(id)))
                .then(Mono.empty());
    }

    public Mono<Void> deleteByName(String name){
        return userR2dbcRepository.deleteByName(name);
    }

    public Mono<User> update(Long id, String name, String email){
//        return userRepository.findById(id)
//                .flatMap(u -> {
//                    u.setName(name);
//                    u.setEmail(email);
//                    return userRepository.save(u);
//                });
        return userR2dbcRepository.findById(id)
                .flatMap(u -> {
                    u.setName(name);
                    u.setEmail(email);
                    return userR2dbcRepository.save(u);
                })
                .flatMap(u -> reactiveRedisTemplate.unlink(getUserCacheKey(id)).then(Mono.just(u))); //unlink는 비동기식 delete, delete는 동기식 delete
    }
}
