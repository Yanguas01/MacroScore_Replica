package es.upm.macroscore.domain.mapper

import es.upm.macroscore.data.network.request.signup.SignUpRequestEntity
import es.upm.macroscore.domain.model.SignUpRequest

interface SignUpRequestMapper : Mapper<SignUpRequest, SignUpRequestEntity>