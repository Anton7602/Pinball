package com.robkov.game.pinball

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.robkov.game.pinball.views.PinballTableView

interface PinballScoreListener {
    fun onScoreChange(newText: Int)
}

class MainActivity : AppCompatActivity(), PinballScoreListener {
    private lateinit var pinballGameView: PinballTableView
    private  lateinit var gameLinearLayout: LinearLayout
    private lateinit var scoreTextView: TextView
    private lateinit var highscoreTextView: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        pinballGameView.setScoreListener(this)
        val scoreHolder= this.getSharedPreferences("Scoreholder", MODE_PRIVATE)
        highscoreTextView.text = "Highscore: ${scoreHolder.getInt("Highscore", 0)}"
    }

    override fun onScoreChange(newScore: Int) {
        scoreTextView.text = "Score: ${newScore}"
        val scoreHolder= this.getSharedPreferences("Scoreholder", MODE_PRIVATE)
        if (newScore > scoreHolder.getInt("Highscore", 0)) {
            var editor = scoreHolder.edit()
            editor.putInt("Highscore", newScore)
            editor.apply()
            highscoreTextView.text = "Highscore: ${newScore}"
        }
    }

    private fun bindViews() {
        gameLinearLayout = findViewById(R.id.gme_pinball_linearLayout)
        pinballGameView = gameLinearLayout.getChildAt(0) as PinballTableView
        scoreTextView = findViewById(R.id.txt_game_score)
        highscoreTextView = findViewById(R.id.txt_game_highscore)
    }
}