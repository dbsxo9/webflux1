package com.example.webflux1.controller;

import com.example.webflux1.dto.UserCreateRequest;
import com.example.webflux1.dto.UserPostResponse;
import com.example.webflux1.dto.UserResponse;
import com.example.webflux1.dto.UserUpdateRequest;
import com.example.webflux1.service.PostServiceV2;
import com.example.webflux1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PostServiceV2 postServiceV2;

    @PostMapping("")
    public Mono<?> createUser(@RequestBody UserCreateRequest request){
        return userService.create(request.getName(), request.getEmail())
                .map(u -> UserResponse.of(u));
    }

    public Flux<UserResponse> findAllUsers(){
        return userService.findAll()
                .map(u -> UserResponse.of(u));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> findUser(@PathVariable Long id){
        return userService.findById(id).map(u -> ResponseEntity.ok(UserResponse.of(u)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable Long id){
        return userService.deleteById(id).then(
                Mono.just(ResponseEntity.noContent().build())
        );

    }

    @DeleteMapping("/search")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable String name){
        return userService.deleteByName(name).then(
                Mono.just(ResponseEntity.noContent().build())
        );

    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request){

        return userService.update(id, request.getName(), request.getEmail())
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @GetMapping("/{id}/posts")
    public Flux<UserPostResponse> getUserPosts(@PathVariable Long id){
        return postServiceV2.findAllByUserId(id)
                .map(p -> UserPostResponse.of(p));
    }

}
