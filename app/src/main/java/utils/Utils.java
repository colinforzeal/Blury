package utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.graphics.Palette;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    public static int getStatusBarHeight(Resources resources){
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Bitmap cropBitmapToCircle(Bitmap bitmap){
        int squareSide;
        if (bitmap.getWidth() > bitmap.getHeight()){
            squareSide = bitmap.getHeight();
        }
        else{
            squareSide = bitmap.getWidth();
        }

        Bitmap output = Bitmap.createBitmap(squareSide, squareSide, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0, squareSide, squareSide);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(squareSide / 2, squareSide / 2, squareSide / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        output = Bitmap.createScaledBitmap(output, 100, 100, true);

        return output;
    }

    public static Bitmap createBackgroundBitmap(Bitmap bitmap, int reqWidth, int reqHeight){
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        float scale = (float) reqHeight / reqWidth;

        int outWidth = (int) (height / scale);
        int newX = (width - outWidth) / 2;

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, newX, 0, outWidth, height);
        return Bitmap.createScaledBitmap(croppedBitmap, reqWidth, reqHeight, true);
    }

    public static int getFabColor(Bitmap bitmap){
        final int defaultColor = 0x000000;
        int result = defaultColor;
        Palette palette;

        try{
            palette = Palette.from(bitmap).generate();
            result = palette.getVibrantColor(defaultColor);
        }
        catch (IllegalArgumentException e){
            Log.e(e.getClass().getName(), "In Utils");
        }

        return result;
    }

    public static String readInputStream(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try{
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
        }
        catch (IOException e){
            Log.e(e.getClass().getName(), "Input failure while reading input stream");
        }

        return stringBuilder.toString();
    }
}
