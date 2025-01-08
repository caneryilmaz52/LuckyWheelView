package com.caneryilmaz.apps.luckywheelview

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.ui.LuckyWheelView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var luckyWheelView: LuckyWheelView
    private lateinit var btnRotate: AppCompatButton

    private val backgroundColorList = arrayListOf(
        Color.parseColor("#00FFFF"),
        Color.parseColor("#00BCD4"),
        Color.parseColor("#F44336"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#FF5722"),
        Color.parseColor("#E91E63"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#FFC107")
    )

    private val textColorList = arrayListOf(
        Color.parseColor("#000000"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FF0000"),
        Color.parseColor("#00FF00"),
        Color.parseColor("#0000FF"),
        Color.parseColor("#00FFFF"),
        Color.parseColor("#FF00FF"),
        Color.parseColor("#FFFF00"),
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

        (0..7).forEach {
            val item = WheelData(
                text = "Item\n#${it + 1}",
                textColor = intArrayOf(
                    textColorList[Random.nextInt(8)],
                ),
                backgroundColor = intArrayOf(
                    backgroundColorList[Random.nextInt(8)],
                ),
                icon = BitmapFactory.decodeResource(resources,R.drawable.star_32dp),
            )
            dummyWheelData.add(item)
        }

        luckyWheelView.drawItemSeparator(true)
        luckyWheelView.setWheelItemSeparatorColor(intArrayOf(
            backgroundColorList[Random.nextInt(8)],
            backgroundColorList[Random.nextInt(8)]
        ))

        luckyWheelView.drawWheelStroke(true)
        luckyWheelView.setWheelStrokeThickness(25F)
        luckyWheelView.setWheelStrokeColor(intArrayOf(
            backgroundColorList[Random.nextInt(8)],
            backgroundColorList[Random.nextInt(8)]
        ))

        luckyWheelView.setWheelCenterTextColor(intArrayOf(
            backgroundColorList[Random.nextInt(8)],
            backgroundColorList[Random.nextInt(8)]
        ))

        luckyWheelView.drawCornerPoints(true)

        luckyWheelView.setWheelData(wheelData = dummyWheelData)

        luckyWheelView.setRotationCompleteListener { wheelData ->
            // do something with winner wheel data
            Toast.makeText(this, wheelData.text, Toast.LENGTH_LONG).show()
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