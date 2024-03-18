package com.example.webflux1.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;

@Repository
@RequiredArgsConstructor
public class PostCustomR2dbcRepositoryImpl implements PostCustomR2dbcRepository{

    private final DatabaseClient databaseClient;

    @Override
    public Flux<Post> findAllByUserId(Long userId) {
        var sql = "SELECT ... FROM posts p LEFT JOIN users u ON p.user_id = u.id WHERE user_id = :userId";
        return databaseClient.sql(sql)
                .bind("userId", userId)
                .fetch()
                .all()
                .map(row -> Post.builder().id((Long) row.get("pid"))
                        .userId((Long) row.get("userId"))
                        .title((String) row.get("title"))
                        .content((String) row.get("content"))
                        .user(User.builder()
                                .id((Long) row.get("uid"))
                                .name((String) row.get("name"))
                                .email((String) row.get("email"))
                                .createdAt(((ZonedDateTime) row.get("uCreatedAt")).toLocalDateTime())
                                .createdAt(((ZonedDateTime)row.get("uUpdatedAt")).toLocalDateTime())
                                .build()
                        )
                        .createdAt(((ZonedDateTime) row.get("uCreatedAt")).toLocalDateTime())
                        .createdAt(((ZonedDateTime)row.get("uUpdatedAt")).toLocalDateTime())
                        .build());
    }
}
