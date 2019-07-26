package shrinkcom.waff.com.interfaces;

import android.content.DialogInterface;

/**
 * Created by administrator on 29/4/17.
 */

public abstract class  DialogBoxButtonListner
{
    public abstract void onYesButtonClick(DialogInterface dialog);

    public void onNoButtonClick(DialogInterface dialog)
    {

        dialog.cancel();
    }

}
