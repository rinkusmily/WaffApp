package shrinkcom.waff.com.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import shrinkcom.waff.com.databinding.MydangerLayoutBinding
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import org.json.JSONObject
import shrinkcom.waff.com.R
import shrinkcom.waff.com.adapter.MyDangerListAdapter
import shrinkcom.waff.com.bean.DangerZone
import shrinkcom.waff.com.interfaces.RecycleViewItemClickListner
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.serverconntion.OkHttpRequest
import shrinkcom.waff.com.serverconntion.WebServices
import shrinkcom.waff.com.util.*
import java.util.ArrayList


class MyDangerListActivity : AppCompatActivity() , OnMapReadyCallback , PermissionsListener  {



    lateinit var mydangerLayoutBinding: MydangerLayoutBinding
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var locationComponent: LocationComponent
    lateinit var mapboxMap: MapboxMap ;
    lateinit var permissionsToRequest: ArrayList<String>
    lateinit var okHttpRequest: OkHttpRequest
    lateinit var dangerZoneArrayList:ArrayList<DangerZone>
    lateinit var sessionManager: SessionManager
    lateinit var sqliteDB: SqliteDB


    lateinit var myDangerListAdapter: MyDangerListAdapter

    lateinit var activity:Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.accessToken));
        mydangerLayoutBinding = DataBindingUtil.setContentView(this , R.layout.mydanger_layout)
        mydangerLayoutBinding.myDangerListActivity = this
        mydangerLayoutBinding.mydangerRecycleview.layoutManager = LinearLayoutManager(this)

        dangerZoneArrayList = ArrayList()

        myDangerListAdapter = MyDangerListAdapter(this , dangerZoneArrayList , itemClickListner)

        mydangerLayoutBinding.mydangerRecycleview.adapter = myDangerListAdapter

        activity = this ;

        sqliteDB = SqliteDB(this)

     //   mydangerLayoutBinding.mapView.onCreate(savedInstanceState);

       // mydangerLayoutBinding.mapView.getMapAsync(this)
        okHttpRequest = OkHttpRequest(this)
        permissionsToRequest = ArrayList()
        sessionManager = SessionManager(this)
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    public fun goBack()
    {
        setResult(Activity.RESULT_OK)

        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)

        finish()
    }

//SATELLITE_STREETS
    override fun onMapReady(mapboxMap: MapboxMap) {

    this.mapboxMap = mapboxMap



    val uniqueStyleUrl = "mapbox://styles/mapbox/navigation-preview-day-v4"

    mapboxMap.setStyle(Style.Builder().fromUrl(uniqueStyleUrl) , object : Style.OnStyleLoaded{
            override fun onStyleLoaded(style: Style)
            {
                enableLocationComponent(style)


                mapboxMap.setInfoWindowAdapter { marker ->


                    val position = CameraPosition.Builder()
                        .target(
                            LatLng(
                                marker.position.latitude,
                                marker.position.longitude
                            )
                        ) // Sets the new camera position
                        .zoom(15.0) // Sets the zoom

                        .build() // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(
                        CameraUpdateFactory
                            .newCameraPosition(position), 1
                    )


                    val v = layoutInflater.inflate(R.layout.marker_delete_layout, null)


                    v
                }


            }

        })

    }

    public override fun onStart() {
        super.onStart()
       // mydangerLayoutBinding.mapView.onStart()
    }

    public override fun onResume() {
        super.onResume()
        //mydangerLayoutBinding.mapView.onResume()
        getDangerZone()
    }

    public override fun onPause() {
        super.onPause()
        //mydangerLayoutBinding.mapView.onPause()
    }

    public override fun onStop() {
        super.onStop()
       // mydangerLayoutBinding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
       // mydangerLayoutBinding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
      //  mydangerLayoutBinding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
       // mydangerLayoutBinding.mapView.onSaveInstanceState(outState)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
           // enableLocationComponent(mapboxMap.getStyle()!!);
        }
    }


    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent =mapboxMap.getLocationComponent()
            locationComponent.activateLocationComponent(this, loadedMapStyle)
            if(UserPermision.checkPermission(this ,permissionsToRequest ))
            locationComponent.setLocationComponentEnabled(true)
            locationComponent.setCameraMode(CameraMode.TRACKING)
            locationComponent.zoomWhileTracking(15.0)
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    //http://shrinkcom.com/waff/api/webservices.php
    //?action=
//action = getDangerZone ()

    fun getDangerZone() {


        dangerZoneArrayList.clear()


        val param : HashMap<String , Any> = HashMap();
        param["action"] = "getDangerZone"
        param["user_id"] = sessionManager.getUser().userId


            okHttpRequest.getResponse(param, object : ServerRespondingListener(this) {

                override fun onError(msg: String) {

                }


                override fun onRespose(resultData: JSONObject) {


                    try {
                        val dangerJsonArrayStr = resultData.getString("userData")
                        val dangerZoneArrayList = Gson().fromJson<ArrayList<DangerZone>>(dangerJsonArrayStr, object :
                            TypeToken<List<DangerZone>>() {

                        }.type)

                        this@MyDangerListActivity.dangerZoneArrayList.addAll(dangerZoneArrayList)

                        myDangerListAdapter.notifyDataSetChanged()

                       // addMarkerOnMap(this@MyDangerListActivity.dangerZoneArrayList)


                    } catch (e1: Exception) {
                    }


                }
            })
        }





     fun deleteMarker(dangerZone:DangerZone)
    {
        val param:HashMap<String , Any> = HashMap()
        param["action"] = "delete_dangerzone"
        param["dangerzone_id"] = dangerZone.id

        okHttpRequest.getResponse(param , object  : ServerRespondingListener(this){
            override fun onRespose(resultData: JSONObject) {

                try {
                    sqliteDB.deleteDanger(""+dangerZone.id)
                    dangerZoneArrayList.remove(dangerZone)
                    myDangerListAdapter.notifyDataSetChanged()
                }
                catch (e:java.lang.Exception)
                {

                }

            }

        })
    }


      val  itemClickListner = object : RecycleViewItemClickListner() {
        override fun onItemClick(pos: Int, status: Int) {


            showDilaodForDanger(pos)
        }

    }

  lateinit var dialog:Dialog

    public fun showDilaodForDanger(pos:Int)
    {

        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.marker_delete_layout)

        dialog.show()

        var cancelBtn :TextView = dialog.findViewById(R.id.cancel_btn);
        var okBtn :TextView = dialog.findViewById(R.id.ok_btn);

        cancelBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v1: View?) {

                dialog.dismiss()



                val intent = Intent(activity, SendCommentActivity::class.java)
                intent.putExtra("snippet",""+dangerZoneArrayList[pos].id)
                intent.putExtra("user_id", ""+sessionManager.getUser().userId)
                intent.putExtra("is_editable" , true)
                activity.startActivity(intent)
            }

        })


        okBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v1: View?) {

                dialog.dismiss()

                deleteMarker(dangerZoneArrayList.get(pos))

            }



        })

    }





}