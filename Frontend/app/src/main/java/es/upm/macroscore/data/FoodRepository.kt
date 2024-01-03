package es.upm.macroscore.data

import es.upm.macroscore.domain.model.FoodModel

class FoodRepository {

    fun getAllFoods(): List<FoodModel> {
        return listOf(
            FoodModel("Manzana1", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana2", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana3", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana4", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana5", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana6", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana7", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana8", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana9", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana10", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana11", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana12", 100.0, 100.0, 100.0, 100.0),
            FoodModel("Manzana13", 100.0, 100.0, 100.0, 100.0),
            FoodModel("ManzanaManzanaManzanaManzanaManzana14", 100.0, 100.0, 100.0, 100.0)
        )
    }

}