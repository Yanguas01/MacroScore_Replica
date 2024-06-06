package es.upm.macroscore.data.mappers

import es.upm.macroscore.data.local.entities.UserEntity
import es.upm.macroscore.data.network.dto.signup.SignUpDTO
import es.upm.macroscore.data.network.dto.signup.UserProfileDTO
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.model.SignUpModel
import es.upm.macroscore.domain.model.UserProfileModel
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

fun SignUpResponse.toUserEntity() = UserEntity(
    id = this.id,
    username = this.username,
    email = this.email,
    gender = this.profile.gender,
    height = this.profile.height,
    weight = this.profile.weight,
    physicalActivityLevel = this.profile.physicalActivityLevel,
    age = this.profile.age
)

fun UserEntity.toSignUpModel(orderMeal: List<String>) = SignUpModel(
    id = this.id,
    username = this.username,
    email = this.email,
    orderMeal = orderMeal,
    profile = UserProfileModel(
        gender = this.gender,
        height = this.height,
        weight = this.weight,
        age = this.age,
        physicalActivityLevel = this.physicalActivityLevel
    )
)