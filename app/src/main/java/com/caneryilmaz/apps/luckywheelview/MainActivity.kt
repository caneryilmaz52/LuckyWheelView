package com.caneryilmaz.apps.luckywheelview

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.ui.LuckyWheelView

class MainActivity : AppCompatActivity() {

    private lateinit var luckyWheelView: LuckyWheelView
    private lateinit var btnRotate: AppCompatButton

    private val backgroundColorList = arrayListOf(
        "#FFFFFF",
        "#00BCD4",
        "#F44336",
        "#9C27B0",
        "#FF5722",
        "#E91E63",
        "#4CAF50",
        "#FFC107"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        luckyWheelView = findViewById(R.id.luckyWheel)
        btnRotate = findViewById(R.id.btnRotate)

        setWheelData()

        btnRotate.setOnClickListener {
            luckyWheelView.rotateWheel()
        }
    }

    private fun setWheelData() {
        val dummyWheelData = ArrayList<WheelData>()

        (0..5).forEach {
            val backgroundColor = Color.parseColor(backgroundColorList[it % 8])
            val textColor = Color.parseColor("#000000")
            val item = WheelData(
                text = "Item\n#${it + 1}",
                textColor = textColor,
                backgroundColor = backgroundColor
            )
            dummyWheelData.add(item)
        }

        luckyWheelView.setWheelData(wheelData = dummyWheelData)

        luckyWheelView.setTargetReachListener { wheelData ->
            // do something with winner wheel data
        }

        luckyWheelView.setRotationStatusListener { status ->
            when (status) {
                RotationStatus.ROTATING -> { // do something
                }

                RotationStatus.IDLE -> { // do something
                }

                RotationStatus.COMPLETED -> { // do something
                }

                RotationStatus.CANCELED -> { // do something
                }
            }
        }
    }
}