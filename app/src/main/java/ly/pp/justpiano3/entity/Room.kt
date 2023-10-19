package ly.pp.justpiano3.entity

class Room(b: Byte, str: String, j: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int) {
    val roomID: Byte
    val roomName: String
    val fcount: Int
    val mcount: Int
    val isPlaying: Int
    val people: IntArray
    var isPeopleFull: Boolean
    val isPassword: Int
    val roomColor: Int
    val roomMode: Int

    companion object {
        const val CAPACITY = 6
    }

    init {
        var i = j
        isPeopleFull = false
        roomID = b
        roomName = str
        mcount = i2
        fcount = i
        isPlaying = i3
        isPassword = i4
        roomMode = i7
        people = IntArray(CAPACITY)
        val notEmptyPositionNum: Int = i + i2 + i6
        if (notEmptyPositionNum == CAPACITY) {
            isPeopleFull = true
        }
        for (i8 in 0 until i) {
            people[i8] = 1
        }
        while (i < notEmptyPositionNum - i6) {
            people[i] = 0
            i++
        }
        for (i8 in CAPACITY - i6 until CAPACITY) {
            people[i8] = 3
        }
        if (!isPeopleFull) {
            for (i8 in notEmptyPositionNum - i6 until CAPACITY - i6) {
                people[i8] = 2
            }
        }
        roomColor = i5
    }
}