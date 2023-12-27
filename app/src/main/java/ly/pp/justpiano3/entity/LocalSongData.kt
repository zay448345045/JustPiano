package ly.pp.justpiano3.entity

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class LocalSongData(var path: String, var isfavo: Int, var score: Int, var lScore: Int) :
    Serializable {

    companion object {
        const val serialVersionUID = 6964277903686951933L
    }

    @Throws(IOException::class)
    private fun writeObject(oos: ObjectOutputStream) {
        oos.writeObject(path)
        oos.writeObject(isfavo)
        oos.writeObject(score)
        oos.writeObject(lScore)
        oos.writeObject(score * lScore xor 2134)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(ois: ObjectInputStream) {
        path = ois.readObject() as String
        isfavo = ois.readObject() as Int
        score = ois.readObject() as Int
        lScore = ois.readObject() as Int
        val sign = ois.readObject() as Int
        if (sign != score * lScore xor 2134) {
            throw IOException("Don't modify data")
        }
    }
}