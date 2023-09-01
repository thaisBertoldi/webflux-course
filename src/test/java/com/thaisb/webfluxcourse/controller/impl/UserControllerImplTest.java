package com.thaisb.webfluxcourse.controller.impl;

import com.mongodb.reactivestreams.client.MongoClient;
import com.thaisb.webfluxcourse.entity.User;
import com.thaisb.webfluxcourse.mapper.UserMapper;
import com.thaisb.webfluxcourse.model.request.UserRequest;
import com.thaisb.webfluxcourse.model.response.UserResponse;
import com.thaisb.webfluxcourse.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    public static final String ID = "123456";
    public static final String EMAIL = "teste@teste.com";
    public static final String NAME = "Teste";
    public static final String PASSWORD = "123";
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private MongoClient mongoClient;

    @Test
    void testSaveWithSuccess() {
        UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);

        when(userService.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post().uri("/users")
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isCreated();

        verify(userService).save(any(UserRequest.class));
    }

    @Test
    void testSaveWithBadRequest() {
        UserRequest request = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);
        webTestClient.post().uri("/users")
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
            .jsonPath("$.error").isEqualTo("Validation error")
            .jsonPath("$.message").isEqualTo("Error on validation attributes")
            .jsonPath("$.errors[0].fieldName").isEqualTo("name")
            .jsonPath("$.errors[0].message").isEqualTo("Campo não pode ter espaços em branco no início ou no final");
    }

    @Test
    void testFindByIdWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(userService.findById(ID)).thenReturn(Mono.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri("/users/" + ID)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(ID)
            .jsonPath("$.name").isEqualTo(NAME)
            .jsonPath("$.email").isEqualTo(EMAIL)
            .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(userService).findById(anyString());
        verify(userMapper).toResponse(any(User.class));
    }

    @Test
    void testFindAllWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(userService.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri("/users")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo(ID)
            .jsonPath("$.[0].name").isEqualTo(NAME)
            .jsonPath("$.[0].email").isEqualTo(EMAIL)
            .jsonPath("$.[0].password").isEqualTo(PASSWORD);

        verify(userService).findAll();
        verify(userMapper).toResponse(any(User.class));
    }

    @Test
    void testUpdateWithSuccess() {
        final var request = new UserRequest(NAME, EMAIL, PASSWORD);
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(userService.update(anyString(), any(UserRequest.class)))
            .thenReturn(Mono.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.patch().uri("/users/" + ID)
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(ID)
            .jsonPath("$.name").isEqualTo(NAME)
            .jsonPath("$.email").isEqualTo(EMAIL)
            .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(userService).update(anyString(), any(UserRequest.class));
        verify(userMapper).toResponse(any(User.class));
    }

    @Test
    void testDeleteWithSuccess() {
        when(userService.deleteById(anyString()))
            .thenReturn(Mono.just(User.builder().build()));

        webTestClient.delete().uri("/users/" + ID)
            .exchange()
            .expectStatus().isOk();

        verify(userService).deleteById(anyString());
    }
}
