import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import com.squareup.picasso.Transformation
import android.graphics.Shader.TileMode


class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = source.width.coerceAtMost(source.height)

        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, TileMode.CLAMP, TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}
