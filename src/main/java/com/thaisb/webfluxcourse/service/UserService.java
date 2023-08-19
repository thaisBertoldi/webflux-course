package com.thaisb.webfluxcourse.service;

import com.thaisb.webfluxcourse.entity.User;
import com.thaisb.webfluxcourse.mapper.UserMapper;
import com.thaisb.webfluxcourse.model.request.UserRequest;
import com.thaisb.webfluxcourse.repository.UserRepository;
import com.thaisb.webfluxcourse.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public Mono<User> save(final UserRequest request) {
        return repository.save(mapper.toEntity(request));
    }

    public Mono<User> findById(final String id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(
                new ObjectNotFoundException(
                    format("Id %s não encontrado.", id))
            ));
    }

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> update(final String id, final UserRequest request) {
        return findById(id)
            .map(entity -> mapper.toEntity(request, entity))
            .flatMap(repository::save);
    }

    public Mono<User> deleteById(final String id) {
        return repository.findAndRemove(id);
    }

}
