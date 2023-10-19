package ly.pp.justpiano3.entity

import java.io.Serializable

class User : Serializable {
    var playerName = ""
    var sex = ""
    var status = ""
    var ishost = ""
    var position = 0.toByte()
    var score = 0
    var combo = 0
    var color = 0
    val level: Int
    var trousers = 0
    var jacket = 0
    var hair = 0
    var eye = 0
    val cl: Int
    var hand = 0
    var cpKind = 0
    var shoes = 0
    var familyID = "0"

    constructor(
        b: Byte,
        str: String,
        hair: Int,
        eye: Int,
        jacket: Int,
        trousers: Int,
        shoes: Int,
        str2: String,
        str3: String,
        str4: String,
        i: Int,
        i2: Int,
        i3: Int,
        i4: Int,
        i5: Int,
        familyID: String
    ) {
        position = b
        playerName = str
        sex = str2
        status = str3
        ishost = str4
        setClothes(hair, eye, jacket, trousers, shoes)
        color = i2
        level = i
        cl = i3
        hand = i4
        cpKind = i5
        this.familyID = familyID
    }

    constructor(
        str: String,
        hair: Int,
        eye: Int,
        jacket: Int,
        trousers: Int,
        shoes: Int,
        str2: String,
        i: Int,
        i2: Int
    ) {
        playerName = str
        sex = str2
        setClothes(hair, eye, jacket, trousers, shoes)
        level = i
        cl = i2
    }

    private fun setClothes(hair: Int, eye: Int, jacket: Int, trousers: Int, shoes: Int) {
        this.hair = hair
        this.eye = eye
        this.jacket = jacket
        this.trousers = trousers
        this.shoes = shoes
    }

    fun setOpenPosition() {
        ishost = "OPEN"
    }
}