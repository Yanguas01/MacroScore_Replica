package es.upm.macroscore.domain.model

sealed class PhysicalActivityLevelModel(displayName: String) {
    data object Sedentary: PhysicalActivityLevelModel("Sedentario")
    data object LightExercise: PhysicalActivityLevelModel("Actividad física ligera")
    data object ModerateExercise: PhysicalActivityLevelModel("Actividad física moderada")
    data object HardExercise: PhysicalActivityLevelModel("Actividad física intensa")
    data object PhysicalJob: PhysicalActivityLevelModel("Actividad física muy intensa")
}
