package es.upm.macroscore.data.mappers

import es.upm.macroscore.data.network.dto.users.UpdatePasswordDTO
import es.upm.macroscore.data.network.dto.users.UserUpdateDTO
import es.upm.macroscore.ui.request.UserUpdatePasswordRequest
import es.upm.macroscore.ui.request.UserUpdateRequest

fun UserUpdateRequest.toDTO() = UserUpdateDTO(
    username = this.username,
    email = this.email,
    gender = this.gender,
    height = this.height,
    weight = this.weight,
    age = this.age,
    physicalActivityLevel = this.physicalActivityLevel
)

fun UserUpdatePasswordRequest.toDTO() = UpdatePasswordDTO(
    oldPassword = this.oldPassword,
    newPassword = this.newPassword
)