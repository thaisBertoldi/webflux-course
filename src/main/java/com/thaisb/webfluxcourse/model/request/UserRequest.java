package com.thaisb.webfluxcourse.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

    @Size(min = 3, max = 50, message = "O nome precisa ter entre 3 e 50 caracteres.")
    @NotBlank(message = "Nome não pode ser nulo ou vazio.")
    String name,
    @Email(message = "Email inválido.")
    @NotBlank(message = "Email nao pode ser nulo ou vazio.")
    String email,
    @Size(min = 3, max = 20, message = "A senha precisa ter entre 3 e 20 caracteres.")
    @NotBlank(message = "Senha não pode ser nulo ou vazio.")
    String password
) {
}
