package es.upm.macroscore.data.mapper

import es.upm.macroscore.data.network.request.login.LogInRequestEntity
import es.upm.macroscore.domain.mapper.LogInRequestMapper
import es.upm.macroscore.domain.model.LogInRequest

class LogInRequestMapperImpl: LogInRequestMapper {
    override fun map(input: LogInRequest): LogInRequestEntity {
        return LogInRequestEntity(
            username = input.username,
            password = input.password,
            scope = input.scope
        )
    }
}