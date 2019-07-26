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
import android.text.TextUtils
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
import shrinkcom.waff.com.databinding.SendCommentLayoutBinding
import shrinkcom.waff.com.interfaces.AppPermissionListener
import shrinkcom.waff.com.interfaces.DialogBoxButtonListner
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.serverconntion.OkHttpRequest
import shrinkcom.waff.com.util.*
import java.io.*

class SendCommentActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var activity: Activity;
    lateinit var sendCommentLayoutBinding: SendCommentLayoutBinding
    lateinit var okHttpRequest: OkHttpRequest
    lateinit var showMessage: ShowMessage
    private var userChoosenTask: String? = null
    lateinit var sessionManager: SessionManager
    var userProfileFile: File? = null

    var isApplyWeb = true;

    lateinit var requestOptions: RequestOptions

    lateinit var permissionList: ArrayList<String>;

    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendCommentLayoutBinding = DataBindingUtil.setContentView(this, R.layout.send_comment_layout)
        sendCommentLayoutBinding.sendCommentActivity = this;
        sendCommentLayoutBinding.backBtn.setOnClickListener(this)
        sendCommentLayoutBinding.infoWindowSend.setOnClickListener(this)
        activity = this
        okHttpRequest = OkHttpRequest(this);
        window.setLayout(Validation.getDeviceWidth(activity) - 80, LinearLayout.LayoutParams.WRAP_CONTENT)
        requestOptions = RequestOptions()
        permissionList = ArrayList<String>()
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionList.add(Manifest.permission.CAMERA)
        isApplyWeb = true
        sessionManager = SessionManager(activity);
        showMessage = ShowMessage(activity)


    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()

        if (isApplyWeb) {
            isApplyWeb = false
            getComment()
        }

    }

    override fun onClick(v: View) {

        if (v.id == R.id.back_btn) {
            finish()
        }

        if (v.id == R.id.info_window_send) {


            if (sendCommentLayoutBinding.infoWindowSend.text.toString().equals("OK")) {
                finish()
            } else {
                if (TextUtils.isEmpty(sendCommentLayoutBinding.commentEditTv.getText().toString())) {
                    showMessage.showDialogMessage("Please enter comment");

                    return;
                }

                if ( !isEditable && userProfileFile == null) {

                    showMessage.showDialogMessage("Please upload a image.");

                    return;
                }

                requestForCommentOnDanger(
                    sendCommentLayoutBinding.commentEditTv.getText().toString(),
                    intent.getStringExtra("snippet")
                )
            }


        }

    }
    //https://shrinkcom.com/waff/

    fun requestForCommentOnDanger(comment: String, dangerZoneId: String) {

        val param = HashMap<String, Any?>()


        // val isEditable = intent.getBooleanExtra("is_editable" , false)

        if (isEditable) {
            param["action"] = "update_comment"

        } else {
            param["action"] = "comment"

        }
        param["comment"] = comment

        param["dangers_id"] = dangerZoneId
        param["user_id"] = sessionManager.getUser().userId

        if (userProfileFile != null)
            param["image"] = userProfileFile




        okHttpRequest.getResponse(param, object : ServerRespondingListener(this) {
            override fun onRespose(resultData: JSONObject) {


                try {


                    showMessage.showDialogMessage(resultData.getString("message"), object : DialogBoxButtonListner() {
                        override fun onYesButtonClick(dialog: DialogInterface?) {

                            finish()

                        }

                    })

                } catch (e: Exception) {
                    val mess: String = "" + e.message;
                    showMessage.showDialogMessage(mess)
                }

            }
        })

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

    public fun requestForPermission() {

        UserPermision.requestForPermission(this, permissionList, object : AppPermissionListener {
            override fun OnAllPermissionsGranted(status: Boolean) {

                if (status) {
                    cameraIntent()
                } else {
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
            if (userProfileFile != null) {
                userProfileFile?.createNewFile()
                fo = FileOutputStream(userProfileFile)
                fo.write(bytes.toByteArray())
                fo.close()
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        requestOptions = requestOptions.transform(CropSquareBitmaTransformation(this))

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
            .into(sendCommentLayoutBinding.imageGallryView)
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
        requestOptions = requestOptions.transform(CropSquareBitmaTransformation(this))

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
            }).into(sendCommentLayoutBinding.imageGallryView)
    }

    //action= getComment (user_id ,dangers_id )

    var isEditable = false

    public fun getComment() {
        val param: HashMap<String, Any?> = HashMap<String, Any?>();





        param["action"] = "getComment"

        param["user_id"] = intent.getStringExtra("user_id")
        param["dangers_id"] = intent.getStringExtra("snippet")

        okHttpRequest.getResponse(param, object : ServerRespondingListener(activity) {

            override fun onError(error: String) {

            }

            override fun onRespose(resultData: JSONObject) {
//
                try {

                    val CommentJson: JSONObject = resultData.getJSONArray("userData").getJSONObject(0)

                    val comment: String = CommentJson.getString("comment")

                    val image = CommentJson.getString("image")

                    isEditable = !TextUtils.isEmpty(comment)


                    if (!TextUtils.isEmpty(comment)) {
                        sendCommentLayoutBinding.commentEditTv.setText(comment)


                        requestOptions = requestOptions.placeholder(R.drawable.image)
                        requestOptions = requestOptions.error(R.drawable.image)
                        requestOptions =
                            requestOptions.transform(CropSquareBitmaTransformation(this@SendCommentActivity))

                        Glide.with(activity).applyDefaultRequestOptions(requestOptions).asBitmap()
                            .load("https://shrinkcom.com/waff/" + image)
                            .listener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    isFirstResource: Boolean
                                ): Boolean {


                                    return false;
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {

                                    return false;
                                }

                            }).into(sendCommentLayoutBinding.imageGallryView);
                    }

                } catch (e: Exception) {

                }

            }

        })


    }


}