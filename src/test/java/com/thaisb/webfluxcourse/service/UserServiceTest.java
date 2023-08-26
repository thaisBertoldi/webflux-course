package com.thaisb.webfluxcourse.service;

import com.thaisb.webfluxcourse.entity.User;
import com.thaisb.webfluxcourse.mapper.UserMapper;
import com.thaisb.webfluxcourse.model.request.UserRequest;
import com.thaisb.webfluxcourse.repository.UserRepository;
import com.thaisb.webfluxcourse.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void testSave() {
        UserRequest request = new UserRequest("teste", "teste@email", "123");
        User entity = User.builder().build();
        when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));

        Mono<User> result = service.save(request);
        StepVerifier.create(result)
            .expectNextMatches(user -> user.getClass() == User.class)
            .expectComplete()
            .verify();

        Mockito.verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testFindById() {
        when(repository.findById(anyString())).thenReturn(Mono.just(User.builder()
            .id("teste")
            .build()));

        Mono<User> result = service.findById("123");

        StepVerifier.create(result)
            .expectNextMatches(user -> user.getClass() == User.class
                && Objects.equals(user.getId(), "teste"))
            .expectComplete()
            .verify();

        Mockito.verify(repository, times(1)).findById("123");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(User.builder().build()));

        Flux<User> result = service.findAll();

        StepVerifier.create(result)
            .expectNextMatches(user -> user.getClass() == User.class)
            .expectComplete()
            .verify();

        Mockito.verify(repository, times(1)).findAll();
    }

    @Test
    void testUpdate() {
        UserRequest request = new UserRequest("teste", "teste@email", "123");
        User entity = User.builder().build();
        when(mapper.toEntity(any(UserRequest.class), any(User.class))).thenReturn(entity);
        when(repository.findById("123")).thenReturn(Mono.just(entity));
        when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        Mono<User> result = service.update("123", request);
        StepVerifier.create(result)
            .expectNextMatches(user -> user.getClass() == User.class)
            .expectComplete()
            .verify();

        Mockito.verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteById() {
        User entity = User.builder().build();
        when(repository.findAndRemove("123")).thenReturn(Mono.just(entity));

        Mono<User> result = service.deleteById("123");
        StepVerifier.create(result)
            .expectNextMatches(user -> user.getClass() == User.class)
            .expectComplete()
            .verify();

        Mockito.verify(repository, times(1)).findAndRemove("123");
    }

    @Test
    void testHandleNotFound() {
        when(repository.findById(anyString())).thenReturn(Mono.empty());
        try {
            service.findById("123").block();
        } catch (Exception exception) {
            assertEquals(ObjectNotFoundException.class, exception.getClass());
            assertEquals(format("Id %s não encontrado.", "123"), exception.getMessage());
        }
    }
}
