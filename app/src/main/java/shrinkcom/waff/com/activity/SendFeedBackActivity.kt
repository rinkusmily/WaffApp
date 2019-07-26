package shrinkcom.waff.com.activity

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import org.json.JSONObject
import shrinkcom.waff.com.R
import shrinkcom.waff.com.databinding.SendFeedbackLayoutBinding
import shrinkcom.waff.com.interfaces.AppPermissionListener
import shrinkcom.waff.com.interfaces.DialogBoxButtonListner
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.serverconntion.OkHttpRequest
import shrinkcom.waff.com.util.*
import java.io.*
import java.util.HashMap

class SendFeedBackActivity :AppCompatActivity()  ,  View.OnClickListener
{
    override fun onClick(v: View) {

        if(v.id == R.id.green_thumb_btn)
        {
            sendFeedbackLayoutBinding.redThumbBtn.setImageResource(R.drawable.red_thumb_inactive);
            sendFeedbackLayoutBinding.greenThumbBtn.setImageResource(R.drawable.green_thumb);
            statusss = "1";
        }

        if(v.id == R.id.red_thumb_btn)
        {
            sendFeedbackLayoutBinding.redThumbBtn.setImageResource(R.drawable.red_thumb);
            sendFeedbackLayoutBinding.greenThumbBtn.setImageResource(R.drawable.green_thumb_inactive);
            statusss = "0";
        }


        if(v.id == R.id.feedback_btn)
        {
            requestForFeedBack(sendFeedbackLayoutBinding.commentEditTv.getText().toString());

        }

    }
    internal var statusss: String = ""


    lateinit var activity: Activity;
    lateinit var sendFeedbackLayoutBinding: SendFeedbackLayoutBinding
    lateinit var okHttpRequest: OkHttpRequest
    lateinit var showMessage: ShowMessage
    private var userChoosenTask: String? = null
     var userProfileFile: File? = null
    lateinit var sqliteDB:SqliteDB

    lateinit var requestOptions: RequestOptions
    lateinit var sessionManager:SessionManager

    lateinit var permissionList:ArrayList<String> ;

    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1

    var feedBackDangerId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendFeedbackLayoutBinding = DataBindingUtil.setContentView(this , R.layout.send_feedback_layout);
        sendFeedbackLayoutBinding.sendFeedBackActivity = this
        feedBackDangerId = intent.getStringExtra("feed_back_danger_id")
        sendFeedbackLayoutBinding.redThumbBtn.setOnClickListener(this)
        sendFeedbackLayoutBinding.feedbackBtn.setOnClickListener(this)
        sendFeedbackLayoutBinding.greenThumbBtn.setOnClickListener(this)

        statusss = "1";
        activity = this
        sessionManager = SessionManager(activity)
        okHttpRequest = OkHttpRequest(activity)
        requestOptions = RequestOptions()

        sqliteDB = SqliteDB(activity)
        window.setLayout(Validation.getDeviceWidth(activity)-80 , LinearLayout.LayoutParams.WRAP_CONTENT)
        permissionList = ArrayList<String>()
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionList.add(Manifest.permission.CAMERA)

    }





    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
    }

    public fun requestForPermission()
    {

        UserPermision.requestForPermission(this , permissionList , object : AppPermissionListener
        {
            override fun OnAllPermissionsGranted(status: Boolean) {

                if(status)
                {
                    cameraIntent()
                }
                else
                {
                    requestForPermission()
                }
            }

        })

    }


    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                userChoosenTask = "Take Photo"

                cameraIntent()
            } else if (items[item] == "Choose from Library") {
                userChoosenTask = "Choose from Library"

                galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data)
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data!!)
        }
    }

    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        userProfileFile = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            userProfileFile?.createNewFile()
            fo = FileOutputStream(userProfileFile)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        requestOptions =  requestOptions.transform(CropSquareBitmaTransformation(this))


        Glide.with(this)
            .setDefaultRequestOptions(requestOptions).asBitmap()
            .load(userProfileFile)

            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }

                override fun onResourceReady(
                    bitmap: Bitmap,
                    model: Any,
                    target: Target<Bitmap>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    // activityEditProfileBinding.imageUserPick.setImageBitmap(bitmap);

                    return false
                }
            })
            .into(sendFeedbackLayoutBinding.imageGallryView)
    }

    private fun onSelectFromGalleryResult(data: Intent?) {


        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
                val bytes = ByteArrayOutputStream()
                userProfileFile = File(
                    Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis().toString() + ".jpg"
                )

                bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

                val fo: FileOutputStream
                try {
                    userProfileFile?.createNewFile()
                    fo = FileOutputStream(userProfileFile)
                    fo.write(bytes.toByteArray())
                    fo.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        requestOptions = requestOptions.placeholder(R.drawable.user)
        requestOptions = requestOptions.error(R.drawable.user)
        requestOptions=   requestOptions.transform(CropSquareBitmaTransformation(this))

        Glide.with(this)
            .setDefaultRequestOptions(requestOptions).asBitmap()
            .load(userProfileFile)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }
            }).into(sendFeedbackLayoutBinding.imageGallryView)
    }


    fun requestForFeedBack(comment: String) {
        val param = HashMap<String, Any?>()
        param["action"] = "feedback"
        param["user_id"] = sessionManager.getUser().userId
        param["comment"] = comment
        param["dangers_id"] = feedBackDangerId
        param.put("status" ,statusss);

        if(userProfileFile != null)
        {
            param.put("image" ,userProfileFile);

        }

        okHttpRequest.getResponse(param, object : ServerRespondingListener(this) {


            override fun onError(error: String) {


                try {
                    showMessage.showDialogMessage(error, object :DialogBoxButtonListner(){
                        override fun onYesButtonClick(dialog: DialogInterface?) {
                            sqliteDB.changeDangerStatus(feedBackDangerId, "" + 0)

                            finish()
                        }

                    })

                } catch (e: Exception) {
                    showMessage.showDialogMessage(e.message.toString())
                }
            }

            override fun onRespose(resultData: JSONObject) {


                try {
                    showMessage.showDialogMessage(resultData.getString("message"), object :DialogBoxButtonListner(){
                        override fun onYesButtonClick(dialog: DialogInterface?) {
                            sqliteDB.changeDangerStatus(feedBackDangerId, "" + 0)

                            finish()
                        }

                    })

                } catch (e: Exception) {
                    showMessage.showDialogMessage(e.message.toString())
                }

                //

            }
        })

    }
}