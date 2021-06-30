package dotd.hmp.data

import android.graphics.Color
import dotd.hmp.Model

object ModelDemoDatas {
    fun getDemoDatas(): List<Model> {
        val list = mutableListOf<Model>()
        list.add(Model("Student", Color.RED))

        return list
    }
}