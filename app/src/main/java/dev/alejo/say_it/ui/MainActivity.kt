package dev.alejo.say_it.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import dev.alejo.say_it.R
import dev.alejo.say_it.core.MyPermission
import dev.alejo.say_it.core.extensions.snack
import dev.alejo.say_it.databinding.ActivityMainBinding
import java.io.File
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private var tts: TextToSpeech? = null
    private val myPermission by lazy { MyPermission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    private fun initComponents() {
        tts = TextToSpeech(this, this)
        binding.btnPlay.setOnClickListener { speak() }
        binding.btnShare.setOnClickListener { validatePermission() }
    }

    private fun speak() {
        binding.inputParent.error = if(binding.input.text!!.isEmpty()) {
            getString(R.string.type_something)
        } else {
            var pitch = binding.pitchSlider.value / 50
            var speed = binding.speedSlider.value / 50
            if(pitch < 0.1) pitch = 0.1f
            if(speed < 0.1) speed = 0.1f
            tts!!.setPitch(pitch)
            tts!!.setSpeechRate(speed)
            tts!!.speak(binding.input.text, TextToSpeech.QUEUE_FLUSH, null, "")
            null
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            tts!!.language = Locale.US
            myPermission.validatePermission()
        } else {
            binding.mainView.snack(this, R.string.init_failed)
        }
    }

    private fun validatePermission() {
        if(myPermission.validatePermission())
            shareAudio()
        else
            binding.mainView.snack(this, R.string.share_unavailable)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun shareAudio() {
        val dir = File(getExternalFilesDir(null), FOLDER_NAME)
        if (!dir.exists())
            dir.mkdir()
        val fileName = "${dir.absolutePath}/$FILE_NAME.mp3"

        tts!!.synthesizeToFile(binding.input.text, null, File(fileName), FILE_NAME)

        val uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
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
        private const val FILE_NAME = "audio"
        private const val FOLDER_NAME = "sayIt"
    }

}