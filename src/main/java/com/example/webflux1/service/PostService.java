package com.example.webflux1.service;

import com.example.webflux1.client.PostClient;
import com.example.webflux1.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    // webClient mvc server request
    private final PostClient postClient;

    public Mono<PostResponse> getPostContent(Long id){
        return postClient.getPost(id);
    }

    public Flux<PostResponse> getMultiplePostContent(List<Long> idList){
        return Flux.fromIterable(idList)
                .flatMap(this::getPostContent);
    }

    public Flux<PostResponse> getParallelMultiplePostContent(List<Long> idList){
        return Flux.fromIterable(idList)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::getPostContent)
                .log()
                .sequential();
    }
}
