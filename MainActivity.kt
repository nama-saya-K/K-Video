package com.k.kvideo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var welcomeOverlay: LinearLayout

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { playMedia(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sembunyikan Bar Navigasi Sistem
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        // Inisialisasi Root Layout UI Programatik
        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(0xFF050505.toInt())
        }

        // Engine PlayerView
        playerView = PlayerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = true
        }
        rootLayout.addView(playerView)

        // Overlay Tampilan Awal K-Video
        welcomeOverlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }

            val titleText = TextView(context).apply {
                text = "K-VIDEO"
                textSize = 36f
                setTextColor(0xFF6366F1.toInt())
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
            }

            val subtitleText = TextView(context).apply {
                text = "Minimalist & Fast Media Player\nAuthor: K"
                textSize = 13f
                setTextColor(0xFF94A3B8.toInt())
                gravity = android.view.Gravity.CENTER
                setPadding(0, 8, 0, 32)
            }

            val openBtn = Button(context).apply {
                text = "📁 Buka File Media (Video/Audio/ISO)"
                setBackgroundColor(0xFF6366F1.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                setOnClickListener {
                    filePickerLauncher.launch("*/*")
                }
            }

            addView(titleText)
            addView(subtitleText)
            addView(openBtn)
        }
        rootLayout.addView(welcomeOverlay)

        setContentView(rootLayout)
        initEngine()
    }

    private fun initEngine() {
        exoPlayer = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(5000)
            .setSeekBackIncrementMs(5000)
            .build().apply {
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Toast.makeText(this@MainActivity, "Gagal memutar file/format tidak terdukung", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        playerView.player = exoPlayer
    }

    private fun playMedia(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        welcomeOverlay.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
