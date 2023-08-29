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
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

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
        UserRequest request = new UserRequest("Teste", "teste@teste.com", "123");

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
        UserRequest request = new UserRequest(" Teste", "teste@teste.com", "123");
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
        final var id = "123";
        final var userResponse = new UserResponse(id, "Teste", "teste@teste.com", "123");

        when(userService.findById("123")).thenReturn(Mono.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri("/users/" + id)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.name").isEqualTo("Teste")
            .jsonPath("$.email").isEqualTo("teste@teste.com")
            .jsonPath("$.password").isEqualTo("123");
    }
}