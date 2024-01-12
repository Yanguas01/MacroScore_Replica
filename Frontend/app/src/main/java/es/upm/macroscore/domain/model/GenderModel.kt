package es.upm.macroscore.domain.model

sealed class GenderModel(val displayName: String) {
    data object Male: GenderModel("Masculino")
    data object Female: GenderModel("Femenino")
}