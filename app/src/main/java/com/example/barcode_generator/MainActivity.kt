//출처: https://www.brightec.co.uk/blog/howto-creating-barcode-kotlin-android

package com.example.barcode_generator

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


val random = Random()
val num = random.nextInt(5)
fun rand(from: Int, to: Int) : Int {
    return random.nextInt(to - from) + from
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayBitmap((9703231641715 / rand(1, 10) * rand(1, 10)).toString())
    }

    fun barcodeRefresh(view: View) {
        displayBitmap((9703231641715 / rand(1, 10) * rand(1, 10)).toString())
    }

    private fun displayBitmap(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_barcode)

        image_barcode.setImageBitmap(
                createBarcodeBitmap(
                        barcodeValue = value,
                        barcodeColor = ResourcesCompat.getColor(getResources(), R.color.design_default_color_primary, null),
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
        val bitMatrix = Code128Writer().encode(
                barcodeValue,
                BarcodeFormat.CODE_128,
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
