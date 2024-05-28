package es.upm.macroscore.domain.mapper

import es.upm.macroscore.data.network.request.login.LogInRequestEntity
import es.upm.macroscore.domain.model.LogInRequest

interface LogInRequestMapper : Mapper<LogInRequest, LogInRequestEntity>