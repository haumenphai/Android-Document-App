package dotd.hmp

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Model {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var color: Int = 0
    var jsonFields: String = ""

    constructor()

    @Ignore
    constructor(name: String, color: Int) {
        this.name = name
        this.color = color
    }

    @Ignore
    constructor(name: String, color: Int, jsonFileds: String) {
        this.name = name
        this.color = color
        this.jsonFields = jsonFields
    }
}