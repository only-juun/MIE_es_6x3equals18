package com.example.barcode_generator

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_generate_qr.*
import java.util.*


class GenerateQR : AppCompatActivity() {
    private var mFirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = mFirebaseAuth.uid.toString()
    val db = FirebaseFirestore.getInstance()
    val random = Random()
    fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr)
        displayBitmap(firebaseUser.toString() +(System.currentTimeMillis()*rand(1,100)/rand(1,100)).toString())
        timer.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val receiveIntent = intent
        val coll_name = receiveIntent.getStringExtra("boxname")
        db.collection(coll_name.toString()).document("QRcode").update("valid", false)
    }

    fun barcodeRefresh(view: View) {
        displayBitmap(firebaseUser.toString() +(System.currentTimeMillis()*rand(1,100)/rand(1,100)).toString())
        RefreshButton.isVisible = false
        RefreshButton.isEnabled = false
        image_barcode.isVisible = true
        text_barcode_number.isVisible = true
        timer.start()
    }

    private val timer = object : CountDownTimer(15500, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            text_refresh_timer.text = ((millisUntilFinished / 1000).toString() + '초')
        }

        override fun onFinish() {
            val receiveIntent = intent
            val coll_name = receiveIntent.getStringExtra("boxname")

            text_refresh_timer.text = "QR코드 재생성"
            image_barcode.isVisible = false
            text_barcode_number.isVisible = false
            RefreshButton.isVisible = true
            RefreshButton.isEnabled = true
            db.collection(coll_name.toString()).document("QRcode").update("valid", false)
        }
    }

    private fun displayBitmap(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_qrcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_qrcode)

        val receiveIntent = intent
        val coll_name = receiveIntent.getStringExtra("boxname")

        db.collection(coll_name.toString()).document("QRcode").set(hashMapOf("valid" to true, "code" to value))
        image_barcode.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = ResourcesCompat.getColor(
                    getResources(),
                    R.color.design_default_color_primary,
                    null
                ),
                backgroundColor = ResourcesCompat.getColor(getResources(), R.color.white, null),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )
        text_barcode_number.text = value
    }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = QRCodeWriter().encode(
            barcodeValue,
            BarcodeFormat.QR_CODE,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }
}
