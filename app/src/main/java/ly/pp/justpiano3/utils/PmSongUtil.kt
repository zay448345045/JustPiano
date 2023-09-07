package ly.pp.justpiano3.utils

import android.content.Context
import ly.pp.justpiano3.entity.PmSongData
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

object PmSongUtil {
    /**
     * pm文件全局速度，目前固定值为5
     */
    private const val PM_GLOBAL_SPEED = 5

    fun parsePmDataByFilePath(context: Context, filePath: String): PmSongData? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.resources.assets.open(filePath)
        } catch (ignore: Exception) {
            try {
                inputStream = FileInputStream(
                    context.filesDir.absolutePath + "/Songs/" + filePath.substring(8)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (inputStream == null) return null
        val pmData = ByteArray(inputStream.available())
        inputStream.read(pmData)
        return parsePmDataByBytes(pmData)
    }

    fun parsePmDataByBytes(pmData: ByteArray): PmSongData? {
        try {
            // 获取曲谱名称
            var songName = "未命名曲谱"
            var songNameLength = 0
            for (i in pmData.indices) {
                if (pmData[i] == '\n'.code.toByte()) {
                    songName = String(pmData, 0, i, StandardCharsets.UTF_8)
                    songNameLength = i
                    break
                }
            }

            // 曲谱时长计算
            var i = songNameLength + 2
            var songTime = 0
            while (i < pmData.size) {
                songTime += pmData[i] * PM_GLOBAL_SPEED
                i += 4
            }
            songTime /= 1000

            // 读取左手难度
            val leftHandDegree = pmData[songNameLength + 1] / 10f

            // 填充音符数据
            val pmDataGroupSize = (pmData.size - (songNameLength + 4)) / 4
            val noteArray = ByteArray(pmDataGroupSize)
            val tickArray = ByteArray(pmDataGroupSize)
            val trackArray = ByteArray(pmDataGroupSize)
            val volumeArray = ByteArray(pmDataGroupSize)
            var i1 = 0
            var i2 = 0
            var i3 = 0
            var i4 = 0
            for (index in songNameLength + 2 until pmData.size - 2) {
                when ((index - songNameLength) % 4) {
                    0 -> {
                        noteArray[i1] = pmData[index]
                        i1++
                    }
                    1 -> {
                        volumeArray[i2] = pmData[index]
                        i2++
                    }
                    2 -> {
                        tickArray[i3] = pmData[index]
                        i3++
                    }
                    3 -> {
                        trackArray[i4] = pmData[index]
                        i4++
                    }
                }
            }

            // 读取右手难度
            val rightHandDegree = pmData[pmData.size - 1] / 10f

            // 数据组装
            return PmSongData(
                songName, leftHandDegree, rightHandDegree, songTime, PM_GLOBAL_SPEED,
                noteArray, tickArray, trackArray, volumeArray
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}