package com.manuni.hello_world.api_integration.api

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream


class ProgressTracker(val mFile: File, val mListener: UploadCallBack):RequestBody() {

    companion object{
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    public interface UploadCallBack{
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    override fun contentType(): MediaType? {
       // return "multipart/form-data".toMediaTypeOrNull()
        return MediaType.parse("multipart/form-data")
    }

    override fun contentLength(): Long {

        return mFile.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)//buffer mane holo koto byte kore pora hocche per byte ei khetre 2KB kore
        val input = FileInputStream(mFile)//tukro tukro kore porar jonno ready kora hocche
        var uploaded:Long = 0
        try {
            var read:Int
            val handler = Handler(Looper.getMainLooper())
            while (true){
                read = input.read(buffer)//buffer size onujayi file ke pora hocche
                if(read == -1) break //porte porte jokhon eksomoy -1 a chole ashbe tokhon loop break hobe
                uploaded += read.toLong()
                sink.write(buffer,0,read)
                handler.post(ProgressUpdater(uploaded,fileLength))

                //if we need timeout, how much time long it should try to upload
                //sink.timeout().timeout(3,TimeUnit.MINUTES)
            }
        }catch (e:Exception){
            mListener.onError()
        }finally {
            input.close()
            mListener.onFinish()
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Long,private val mTotal:Long):Runnable{
        override fun run() {
            mListener.onProgressUpdate((100*mUploaded/mTotal).toInt())
        }
    }

}