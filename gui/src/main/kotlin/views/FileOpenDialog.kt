package views

import app.logger.Log
import app.logger.Logger
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil.memAllocPointer
import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.util.nfd.NativeFileDialog.*


class FileOpenDialog {
    fun open(filterList: String): String {
        val outPath = memAllocPointer(1)
        return try {
            checkResult(
                NFD_OpenDialog(filterList, "", outPath),
                outPath
            )
        } finally {
            memFree(outPath)
        }
    }

    private fun checkResult(result: Int, path: PointerBuffer): String {
        when (result) {
            NFD_OKAY -> {
                nNFD_Free(path[0])
                return path.stringUTF8
            }
            NFD_CANCEL -> {}
            else -> Logger.log(NFD_GetError().orEmpty(), Log.Level.ERROR)
        }
        return ""
    }
}