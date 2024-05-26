package es.upm.macroscore.data.mapper

import es.upm.macroscore.data.network.request.signup.SignUpRequestEntity
import es.upm.macroscore.data.network.request.signup.UserProfileRequestEntity
import es.upm.macroscore.domain.mapper.SignUpRequestMapper
import es.upm.macroscore.domain.model.SignUpRequest

class SignUpRequestMapperImpl: SignUpRequestMapper {
    override fun map(input: SignUpRequest): SignUpRequestEntity {
        return SignUpRequestEntity(
            username = input.username,
            email = input.email,
            password = input.password,
            profile = UserProfileRequestEntity(
                gender = input.profile.gender,
                height = input.profile.height,
                weight = input.profile.weight,
                age = input.profile.age,
                physicalActivityLevel = input.profile.physicalActivityLevel
            )
        )
    }
}