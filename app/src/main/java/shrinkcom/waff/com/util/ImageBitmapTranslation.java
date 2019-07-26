package shrinkcom.waff.com.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by administrator on 7/3/17.
 */

public class ImageBitmapTranslation extends BitmapTransformation
{
    int desireWidth;
    public ImageBitmapTranslation(Context context , int desireWidth)
    {

        this.desireWidth = desireWidth;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap bitmap, int outWidth, int outHeight) {


        int  modifiedBitmapWidth = bitmap.getWidth();
        int modifiedBitmapHeight = bitmap.getHeight() ;

        if (desireWidth >= bitmap.getWidth() && desireWidth >= bitmap.getHeight())
        {
              modifiedBitmapWidth = bitmap.getWidth();
             modifiedBitmapHeight = bitmap.getHeight() ;
        }
        else
        {
            if (desireWidth < bitmap.getWidth())
            {
                modifiedBitmapWidth = desireWidth ;

                float widthRatio = bitmap.getWidth() / (float)desireWidth ;

                modifiedBitmapHeight =  (int)(bitmap.getHeight() / widthRatio) ;

            }
            else
            {
                if (desireWidth < bitmap.getHeight())
                {
                    modifiedBitmapHeight = desireWidth ;

                    float heightRatio = bitmap.getHeight() / (float)desireWidth ;

                    modifiedBitmapWidth =  (int)(bitmap.getHeight() / heightRatio) ;

                }
            }
        }


        bitmap = Bitmap.createScaledBitmap(bitmap, modifiedBitmapWidth, modifiedBitmapHeight, true);




        return bitmap;

    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
