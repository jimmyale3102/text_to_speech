package dev.alejo.texttospeach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import dev.alejo.texttospeach.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    private fun initComponents() {
        tts = TextToSpeech(this, this)
        binding.btnPlay.setOnClickListener { speak() }
    }

    private fun speak() {
        binding.inputParent.error = if(binding.input.text!!.isEmpty()) {
            getString(R.string.type_something)
        } else {
            tts!!.speak(binding.input.text, TextToSpeech.QUEUE_FLUSH, null, "")
            null
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS)
            tts!!.language = Locale.US
    }

    override fun onDestroy() {
        if(tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

}