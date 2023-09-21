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
    val roomKuang: Int
    val roomMode: Int

    companion object {
        private const val peopleCapacity = 6
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
        people = IntArray(peopleCapacity)
        val notEmptyPositionNum: Int = i + i2 + i6
        if (notEmptyPositionNum == peopleCapacity) {
            isPeopleFull = true
        }
        for (i8 in 0 until i) {
            people[i8] = 1
        }
        while (i < notEmptyPositionNum - i6) {
            people[i] = 0
            i++
        }
        for (i8 in peopleCapacity - i6 until peopleCapacity) {
            people[i8] = 3
        }
        if (!isPeopleFull) {
            for (i8 in notEmptyPositionNum - i6 until peopleCapacity - i6) {
                people[i8] = 2
            }
        }
        roomKuang = i5
    }
}