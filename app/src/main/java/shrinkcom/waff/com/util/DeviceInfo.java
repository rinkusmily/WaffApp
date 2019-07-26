package shrinkcom.waff.com.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by administrator on 28/7/17.
 */

public class DeviceInfo
{
    public static int getDeviceWidth(Activity activity)
    {
        Display display =activity. getWindowManager().getDefaultDisplay();
        int  screenWidth = display.getWidth();
        return screenWidth ;
    }

    public static int getDeviceHeight(Activity activity)
    {
        Display display =activity. getWindowManager().getDefaultDisplay();
        int  screenHeight = display.getHeight();
        return screenHeight ;
    }


    public static int getDensity(Activity activity)
    {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int densityDpi = (int)(metrics.density * 160f);

        return densityDpi ;

    }

}
