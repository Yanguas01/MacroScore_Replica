package es.upm.macroscore.data.mappers

import es.upm.macroscore.data.network.dto.login.LogInDTO
import es.upm.macroscore.domain.request.LogInRequest

fun LogInRequest.toDTO(): LogInDTO = LogInDTO(
    username = this.username,
    password = this.password,
    scope = this.scope
)

