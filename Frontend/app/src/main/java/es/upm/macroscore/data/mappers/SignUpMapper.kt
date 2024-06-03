package es.upm.macroscore.data.mappers

import es.upm.macroscore.data.network.dto.signup.SignUpDTO
import es.upm.macroscore.data.network.dto.signup.UserProfileDTO
import es.upm.macroscore.domain.request.SignUpRequest
import es.upm.macroscore.domain.request.UserProfileRequest

fun SignUpRequest.toDTO() = SignUpDTO(
    username = this.username,
    email = this.email,
    password = this.password,
    profile = this.profile.toDTO()
)

fun UserProfileRequest.toDTO() = UserProfileDTO(
    gender = this.gender,
    height = this.height,
    weight = this.weight,
    age = this.age,
    physicalActivityLevel = this.physicalActivityLevel
)