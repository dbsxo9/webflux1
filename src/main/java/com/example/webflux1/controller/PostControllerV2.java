package com.example.webflux1.controller;

import com.example.webflux1.dto.PostCreatedRequest;
import com.example.webflux1.dto.PostResponseV2;
import com.example.webflux1.service.PostServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v2/posts")
@RequiredArgsConstructor
public class PostControllerV2 {
    private final PostServiceV2 postServiceV2;

    @PostMapping()
    public Mono<PostResponseV2> createPost(@RequestBody PostCreatedRequest request) {
        return postServiceV2.create(request.getUserId(), request.getTitle(), request.getContent())
                .map(i -> PostResponseV2.of(i));
    }

    @GetMapping("")
    public Flux<PostResponseV2> findAllPost(){
        return postServiceV2.findAll().map(r -> PostResponseV2.of(r));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostResponseV2>> findPost(@PathVariable Long id){
        return postServiceV2.findById(id)
                .map(p -> ResponseEntity.ok().body(PostResponseV2.of(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                ;
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<PostResponseV2>> deletePost(@PathVariable Long id){
        return postServiceV2.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}