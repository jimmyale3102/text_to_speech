package dev.alejo.texttospeach

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import dev.alejo.texttospeach.databinding.ActivityMainBinding
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private var tts: TextToSpeech? = null

    private var mAudioFilename = ""
    private val mUtteranceID = "totts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    private fun initComponents() {
        tts = TextToSpeech(this, this)
        binding.btnPlay.setOnClickListener { speak() }
        binding.btnShare.setOnClickListener { shareAudio() }
    }

    private fun speak() {
        binding.inputParent.error = if(binding.input.text!!.isEmpty()) {
            getString(R.string.type_something)
        } else {
            tts!!.speak(binding.input.text, TextToSpeech.QUEUE_FLUSH, null, "")
            null
        }
    }

    private fun createFile() {
        // Perform the dynamic permission request
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
        )


    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            tts!!.language = Locale.US
            createFile()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun shareAudio() {
        // Create audio file location
/*
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/textToSpeach/")
        if(!dir.exists())
            dir.mkdir()
        mAudioFilename = "${dir.absolutePath}/audio3.mp3"
        */
        val dir = File(getExternalFilesDir(null), "textToSpeech")
        if (!dir.exists())
            dir.mkdir()
        val fileName = "${dir.absolutePath}/audio.mp3"

        tts!!.synthesizeToFile(binding.input.text, null, File(fileName), "audio");
        Log.d("Saved to ->", fileName)

        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            File(fileName)
        )

        val share = Intent(Intent.ACTION_SEND)
        share.type = "audio/*"
        share.putExtra(Intent.EXTRA_STREAM, uri)

        val chooser = Intent.createChooser(share, "Share File")
        val resInfoList =
            this.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(chooser)
    }

    override fun onDestroy() {
        if(tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 123
    }

}