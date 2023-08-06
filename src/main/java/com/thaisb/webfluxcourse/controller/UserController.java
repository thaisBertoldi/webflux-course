package com.thaisb.webfluxcourse.controller;

import com.thaisb.webfluxcourse.model.request.UserRequest;
import com.thaisb.webfluxcourse.model.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserController {

    @PostMapping
    ResponseEntity<Mono<Void>> save(@RequestBody UserRequest request);

    @GetMapping(value = "/{id}")
    ResponseEntity<Mono<UserResponse>> findById(@PathVariable String id);

    @GetMapping
    ResponseEntity<Flux<UserResponse>> findAll();

    @PatchMapping(value = "/{id}")
    ResponseEntity<Mono<UserResponse>> update(@PathVariable String id, @RequestBody UserRequest userRequest);

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Mono<Void>> deleteById(@PathVariable String id);

}