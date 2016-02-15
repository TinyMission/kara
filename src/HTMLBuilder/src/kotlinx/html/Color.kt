package kotlinx.html

import java.util.*


/** Container class for HSL values */
class HslValues(var hue: Double, var saturation: Double, var lightness: Double) {

    /** Sets the lightness value while ensuring it stays within range. */
    fun safeSetLightness(l: Double) {
        lightness = Math.min(1.0, Math.max(0.0, l))
    }

    /** Sets the saturation value while ensuring it stays within range. */
    fun safeSetSaturation(l: Double) {
        saturation = Math.min(1.0, Math.max(0.0, l))
    }
}


/** A general color class that stores colors as floating point RGBA values.
 */
class Color(var red: Double, var green: Double, var blue: Double, var alpha: Double = 1.0) {
    companion object {

        /** Creates a color from integer RGBA values between 0 and 255. */
        public fun fromRgb(red: Int, green: Int, blue: Int, alpha: Int = 255): Color {
            return Color(red.toDouble() / 255, green.toDouble() / 255.0, blue.toDouble() / 255.0, alpha.toDouble() / 255.0)
        }

        /** Makes a color from a hex string (hash included). */
        public fun fromHex(s : String) : Color {
            // TODO 4/8-digit are actually not supported by CSS spec, make framework parse rgb/rgba() format instead

            if (s.length == 4 && s[0] == '#') {
                val r = Integer.parseInt(s.substring(1, 2), 16)
                val g = Integer.parseInt(s.substring(2, 3), 16)
                val b = Integer.parseInt(s.substring(3, 4), 16)
                return Color.fromRgb(r * 16 + r, g * 16 + g, b * 16 + b)
            }
            if (s.length == 5 && s[0] == '#') {
                val r = Integer.parseInt(s.substring(1, 2), 16)
                val g = Integer.parseInt(s.substring(2, 3), 16)
                val b = Integer.parseInt(s.substring(3, 4), 16)
                val a = Integer.parseInt(s.substring(4, 5), 16)
                return Color.fromRgb(r * 16 + r, g * 16 + g, b * 16 + b, a * 16 + a)
            }
            if (s.length == 7 && s[0] == '#') {
                val r = Integer.parseInt(s.substring(1, 3), 16)
                val g = Integer.parseInt(s.substring(3, 5), 16)
                val b = Integer.parseInt(s.substring(5, 7), 16)
                return Color.fromRgb(r, g, b)
            }
            if (s.length == 9 && s[0] == '#') {
                val r = Integer.parseInt(s.substring(1, 3), 16)
                val g = Integer.parseInt(s.substring(3, 5), 16)
                val b = Integer.parseInt(s.substring(5, 7), 16)
                val a = Integer.parseInt(s.substring(7, 9), 16)
                return Color.fromRgb(r, g, b, a)
            }
            throw Exception("Invalid color hex string: $s");
        }

        /** Creates a color from a set of HSL values. */
        fun fromHsl(hsl: HslValues): Color {
            val color = Color(0.0, 0.0, 0.0)
            color.setHsl(hsl)
            return color
        }

    }

    /** Creates a copy of the color. */
    fun copy(): Color {
        return Color(red, green, blue)
    }

    var redInt: Int
        get() = (red * 255.0).toInt()
        set(value) { red = value.toDouble() / 255.0 }

    var greenInt: Int
        get() = (green * 255.0).toInt()
        set(value) { green = value.toDouble() / 255.0 }

    var blueInt: Int
        get() = (blue * 255.0).toInt()
        set(value) { blue = value.toDouble() / 255.0 }

    var alphaInt: Int
        get() = (alpha * 255.0).toInt()
        set(value) { alpha = value.toDouble() / 255.0}

    private fun Int.twoDigitHex(): String = (if (this < 16) "0" else "") + Integer.toHexString(this)

    val hexString: String
        get() = "#${redInt.twoDigitHex()}${greenInt.twoDigitHex()}${blueInt.twoDigitHex()}"

    override fun toString(): String {
        if (alpha < 1.0) {
            return "rgba($redInt, $greenInt, $blueInt, ${java.lang.String.format(Locale.ENGLISH, "%.3f", alpha)})"
        }
        else {
            return hexString
        }
    }


    /** Generate HSL values based the current RGB values. */
    fun toHsl(): HslValues {
        val max = Math.max(Math.max(red, green), blue)
        val min = Math.min(Math.min(red, green), blue)
        val avg = (max + min) / 2
        val hsl = HslValues(avg, avg, avg)

        if (max == min) {
            // achromatic
            hsl.hue = 0.0
            hsl.saturation = 0.0
        } else {
            val d = max - min
            if (hsl.lightness > 0.5)
                hsl.saturation = d / (2 - max - min)
            else
                hsl.saturation = d / (max + min)
            when (max) {
                red -> {
                    hsl.hue = (green - blue) / d
                    if (green < blue)
                        hsl.hue += 6.0
                }
                green -> hsl.hue = (blue - red) / d + 2
                blue -> hsl.hue = (red - green) / d + 4
                else -> {
                }
            }
            hsl.hue /= 6.0
        }
        return hsl
    }

    fun setHsl(hsl: HslValues) {
        if (hsl.saturation == 0.0) {
            // achromatic
            red = hsl.lightness
            green = hsl.lightness
            blue = hsl.lightness
        } else {
            fun hue2rgb(p: Double, q: Double, _t: Double): Double {
                var t = _t
                if(t < 0.0)
                    t += 1.0
                if(t > 1.0)
                    t -= 1.0
                if(t < 1.0 / 6.0)
                    return p + (q - p) * 6.0 * t;
                if(t < 0.5)
                    return q;
                if(t < 2.0 / 3.0)
                    return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
                return p;
            }
            var q: Double
            if (hsl.lightness < 0.5)
                q = hsl.lightness * (1 + hsl.saturation)
            else
                q = hsl.lightness + hsl.saturation - hsl.lightness * hsl.saturation
            var p = 2.0 * hsl.lightness - q;
            red = hue2rgb(p, q, hsl.hue + 1.0 / 3.0);
            green = hue2rgb(p, q, hsl.hue);
            blue = hue2rgb(p, q, hsl.hue - 1.0 / 3.0);
        }
    }


    /** Increases the lightness of the color by the given amount (should be between 0 and 1). */
    fun lighten(dl: Double): Color {
        val hsl = toHsl()
        hsl.safeSetLightness(hsl.lightness + dl)
        setHsl(hsl)
        return this
    }

    /** Decreases the lightness of the color by the given amount (should be between 0 and 1). */
    fun darken(dl: Double): Color {
        val hsl = toHsl()
        hsl.safeSetLightness(hsl.lightness - dl)
        setHsl(hsl)
        return this
    }

    /** Increases the saturation of the color by the given amount (should be between 0 and 1). */
    fun saturate(dl: Double): Color {
        val hsl = toHsl()
        hsl.safeSetSaturation(hsl.lightness + dl)
        setHsl(hsl)
        return this
    }

    /** Decreases the saturation of the color by the given amount (should be between 0 and 1). */
    fun desaturate(dl: Double): Color {
        val hsl = toHsl()
        hsl.safeSetSaturation(hsl.lightness - dl)
        setHsl(hsl)
        return this
    }

}

/** Smart helper function that creates a color from a string.
 * Currently, s can be:
 *  - a 3 digit hex string for RGB: #F9B
 *  - a 4 digit hex string for RGBA: #F9B8
 *  - a 6 digit hex string for RGB: #FE395A
 *  - a 8 digit hex string for RGBA: #FE395A88
 */
inline fun color(s: String): Color {
    if (s.startsWith("#"))
        return Color.fromHex(s)
    throw Exception("Invalid color string: $s");
}

/** Returns true if the string is a valid color literal. */
inline fun isColor(s: String): Boolean {
    return s.startsWith("#") || s.startsWith("rgb")
}

