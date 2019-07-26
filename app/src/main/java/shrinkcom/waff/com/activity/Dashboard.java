package shrinkcom.waff.com.activity;

import android.Manifest;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Placeholder;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.DangerZone;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.bean.WaffAddress;
import shrinkcom.waff.com.broadcastreceivers.SaveDataSqliteBroadcast;
import shrinkcom.waff.com.databinding.DashboardLayoutBinding;
import shrinkcom.waff.com.interfaces.DialogBoxButtonListner;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.serverconntion.WebServices;
import shrinkcom.waff.com.util.*;
import timber.log.Timber;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.maps.Style.MAPBOX_STREETS;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class Dashboard extends AppCompatActivity implements
        View.OnClickListener,
        OnMapReadyCallback,
        MapboxMap.OnMapLongClickListener,
        PermissionsListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {


    public static final int OriginAddressRequest = 4000;
    public static final int DestinationAddressRequest = 5000;
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String TAG = "DirectionsActivity";
    public UserData userData;
    DashboardLayoutBinding dashboardLayoutBinding;
    Activity activity;
    ShowMessage showMessage;
    SessionManager sessionManager;
    LatLng dangerPoints;
    ArrayList<String> permissionsToRequest;
    ArrayList<DangerZone> dangerZoneArrayList;
    LatLng mCurrentLatLong;
    OkHttpRequest okHttpRequest;
    Handler handler;
    GoogleApiClient googleApiClient;
    MapboxMap mapboxMap;
    LocationRequest mLocationRequest;
    RequestOptions requestOptions;
    Timer timer;
    private SqliteDB sqliteDB;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private NavigationMapRoute navigationMapRoute;

    ////////////////
    Layer roadLayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.accessToken));
        dashboardLayoutBinding = DataBindingUtil.setContentView(this, R.layout.dashboard_layout);
        showMessage = new ShowMessage(this);
        activity = this;
        sessionManager = new SessionManager(activity);
        dashboardLayoutBinding.logoutBtn.setOnClickListener(this);
        sqliteDB = new SqliteDB(this);
        dashboardLayoutBinding.fab.setOnClickListener(this);
        okHttpRequest = new OkHttpRequest(this);
        dangerZoneArrayList = new ArrayList<>();
        permissionsToRequest = new ArrayList<>();
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        dashboardLayoutBinding.waffMapView.onCreate(savedInstanceState);
        dashboardLayoutBinding.waffMapView.getMapAsync(this);
        dashboardLayoutBinding.textviewSetting.setOnClickListener(this);
        handler = new Handler(getMainLooper());
        dashboardLayoutBinding.sideMenuBtn.setOnClickListener(this);
        ;
        dashboardLayoutBinding.setDashboard(this);

       // showNotification("djhsdfs");

        try
        {
            String address = GeoCoderDataParser.getAddressFromLatitudeLongitude(activity, sessionManager.getDestinationAddresss().getLatitude(),sessionManager.getDestinationAddresss().getLongitude() );
            dashboardLayoutBinding.tvDestinationAddress.setText(""+address);

        }
        catch (Exception e)
        {

        }


        /*dashboardLayoutBinding.favVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.bottom_location_volufav, null);
                BottomSheetDialog dialog = new BottomSheetDialog(Dashboard.this);
                dialog.setContentView(view);
                dialog.show();
                Button buttonvoice = (Button) dialog.findViewById(R.id.buttonvoice);
                buttonvoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       *//* Intent voice = new Intent(Dashboard.this,ActivityVoiceDirection.class);
                        startActivity(voice);*//*
                    }
                });
                LinearLayout soundon = (LinearLayout) dialog.findViewById(R.id.soundon);
                soundon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       *//* MediaPlayer mPlayer = MediaPlayer.create(Dashboard.this, R.raw.kitkat);
                        mPlayer.start();*//*
                    }
                });
                LinearLayout soundoff = (LinearLayout) dialog.findViewById(R.id.soundoff);
                soundoff.setOnClickListener(view1 -> Toast.makeText(Dashboard.this, "Sound off",
                        Toast.LENGTH_LONG).show());

            }
        });*/


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.side_menu_btn) {
            drawablesidePanel();
        }

        if (v.getId() == R.id.logout_btn) {
            logout();
        }


        if (v.getId() == R.id.fab) {

            dangerPoints = new LatLng(mCurrentLatLong.getLatitude(), mCurrentLatLong.getLongitude());
            Intent intent = new Intent(getApplicationContext(), SelectReportActivity.class);
            startActivityForResult(intent, 2000);


        }
        if (v.getId() == R.id.textview_setting) {

            startActivity(new Intent(activity, SettingActivity.class));


        }


    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {


    }

    public void drawablesidePanel() {
        if (dashboardLayoutBinding.drawableLayout.isDrawerOpen(Gravity.START)) {
            dashboardLayoutBinding.drawableLayout.closeDrawer(Gravity.START);
        } else {
            dashboardLayoutBinding.drawableLayout.openDrawer(Gravity.START);

        }
    }

    public void logout() {
        showMessage.showOptionalDailogBox("Alert!", "Are you sure you want to logout?", new DialogBoxButtonListner() {
            @Override
            public void onYesButtonClick(DialogInterface dialog) {

                dialog.cancel();
                sqliteDB.clearTable();

                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                sessionManager.clear();
            }
        });
    }

    @Override
    public boolean onMapLongClick(LatLng point) {

        dangerPoints = point;

        Intent intent = new Intent(getApplicationContext(), SelectReportActivity.class);
        startActivityForResult(intent, 2000);

        return true;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {


        setUpMapBox(mapboxMap);


        if (checkDrawPathEgibility()) {
            getRoute(new LatLng(sessionManager.getDestinationAddresss().getLatitude() , sessionManager.getDestinationAddresss().getLongitude()));
        }

        mapboxMap.setOnInfoWindowClickListener(marker -> false);

        mapboxMap.setInfoWindowAdapter(marker -> {


            View v = getLayoutInflater().inflate(R.layout.layout_info_window, null);
            ImageView dangerImageView = v.findViewById(R.id.danger_image_view);
            TextView infoWindowSend = v.findViewById(R.id.info_window_send);
            TextView commentListView = v.findViewById(R.id.comment_listview);

            TextView infoWindowTitle = v.findViewById(R.id.info_window_title);
            TextView infoWindowDescription = v.findViewById(R.id.info_window_description);

            try
            {
                String address = GeoCoderDataParser.getAddressFromLatitudeLongitude(activity , marker.getPosition().getLatitude() , marker.getPosition().getLongitude());
                infoWindowDescription.setText(address);


                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude())) // Sets the new camera position
                        .zoom(15) // Sets the zoom

                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 1);




                DangerZone dangerZone = sqliteDB.getDangerById(marker.getSnippet());

                String url = "https://shrinkcom.com/waff/admin/images/dangerzone/" + dangerZone.getImage();
                RequestOptions requestOptions1 = new RequestOptions();
                requestOptions1 = requestOptions1.error(R.drawable.carpool_home_work_illu);
                requestOptions1 = requestOptions1.placeholder(R.drawable.carpool_home_work_illu);
                requestOptions1 = requestOptions1.transform(new CircleBitmapTranslation(this));

                Glide.with(this)
                        .setDefaultRequestOptions(requestOptions1).asBitmap()
                        .load(url)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        })
                        .into(dangerImageView);

            }
            catch (Exception e)
            {
                infoWindowDescription.setVisibility(View.GONE);
            }




            infoWindowTitle.setText(marker.getTitle());


            infoWindowSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(marker.getTitle())) {
                        Intent intent = new Intent(getApplicationContext(), SendCommentActivity.class);
                        intent.putExtra("snippet", marker.getSnippet());
                        intent.putExtra("user_id", sessionManager.getUser().getUserId());

                        startActivity(intent);


                    }
                }
            });


            commentListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), CommentListActivity.class);
                    intent.putExtra("id", marker.getSnippet());

                    startActivity(intent);

                }
            });
            // v.setVisibility(View.GONE); //




            return v;
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        dashboardLayoutBinding.waffMapView.onStart();
        fireAlaramIntent();


    }

    @Override
    protected void onResume() {
        super.onResume();
        dashboardLayoutBinding.waffMapView.onResume();


        try {
            userData = sessionManager.getUser();
            dashboardLayoutBinding.userNameTv.setText(userData.getUsername());


            dashboardLayoutBinding.userActionTv.setText(userData.getOutsideAction());


            requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CircleBitmapTranslation(this));
            requestOptions = requestOptions.placeholder(R.drawable.user);
            requestOptions = requestOptions.error(R.drawable.user);


            String url = "https://shrinkcom.com/waff/" + sessionManager.getUser().getImage();


            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions).asBitmap()
                    .load(url)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .into(dashboardLayoutBinding.imageUser);


        } catch (Exception e) {

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        dashboardLayoutBinding.waffMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dashboardLayoutBinding.waffMapView.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dashboardLayoutBinding.waffMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dashboardLayoutBinding.waffMapView.onDestroy();
        stopLocationUpdates();
        stopAlaramIntent();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dashboardLayoutBinding.waffMapView.onLowMemory();
    }

    public void setUpMapBox(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        String uniqueStyleUrl = "mapbox://styles/mapbox/navigation-preview-day-v4";
         String SATELLITE_STREETS = "mapbox://styles/mapbox/streets-v11";

       // Style.StyleUrl mapbox://styles/mapbox/satellite-streets-v11









        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/mapbox/outdoors-v9"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {


                mapboxMap.addOnMapLongClickListener(Dashboard.this);

               List<Layer> layerArrayList =  style.getLayers();


              /* for (Layer layer :  layerArrayList)
               {
                   Log.e("ids" , layer.getId());




                   layer.setProperties(
                           PropertyFactory.textColor(getResources().getColor(R.color.black)),
                           PropertyFactory.fillColor(Color.parseColor("#00FF08")));

               }*/

               //





                //  enableLocationComponent(style);

                googleApiClient = new GoogleApiClient.Builder(activity)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(Dashboard.this)
                        .addOnConnectionFailedListener(Dashboard.this)
                        .build();

                if (googleApiClient != null) {
                    googleApiClient.connect();
                }


            }
        });
    }

    public void setCurrentLocation(double latitude, double logitude) {
        try {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(latitude, logitude)) // Sets the new camera position
                    .zoom(15) // Sets the zoom

                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 7000);


            if (sessionManager.getoriginAddress().getLatitude() == 0.0 || sessionManager.getoriginAddress().getLongitude() == 0.0) {
                WaffAddress waffAddress = new WaffAddress();
                waffAddress.setLatitude(latitude);
                waffAddress.setLongitude(logitude);
                waffAddress.setGoogleAddress("no");
                sessionManager.saveOridinAddress(waffAddress.getLatitude(), waffAddress.getLongitude(), waffAddress.getGoogleAddress());

            }


            IconFactory iconFactory = IconFactory.getInstance(Dashboard.this);
            Icon icon = iconFactory.fromResource(R.drawable.current_loc);



            iconFactory.fromPath("");


            mapboxMap.addMarker(new MarkerOptions()
                    .title("")
                    .icon(icon)
                    .position(new LatLng(latitude, logitude)));

            String address = GeoCoderDataParser.getAddressFromLatitudeLongitude(activity, latitude, logitude);

            dashboardLayoutBinding.tvOriginAddress.setText("" + address);


           /*Bitmap currentLocation =  BitmapFactory.decodeResource(
                   Dashboard.this.getResources(), R.drawable.current_loc);


           mapboxMap.getStyle().addImage("marker-icon-id",
                   currentLocation);

           GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
                   Point.fromLngLat(logitude, latitude)));



           mapboxMap.getStyle().addSource(geoJsonSource);

           SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
           symbolLayer.withProperties(
                   iconImage("marker-icon-id"),
                   iconSize(1f),
                   iconIgnorePlacement(true),
                   iconAllowOverlap(true)

           );


           mapboxMap.getStyle().addLayer(symbolLayer);*/
        } catch (Exception e) {

        }


    }




    public void addMarkerOnMap(DangerZone dangerZone) {


        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(getResources().getString(R.string.accessToken))
                .query(Point.fromLngLat(dangerZone.getLongitude(), dangerZone.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                .mode(GeocodingCriteria.MODE_PLACES)
                .build();

        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call,
                                   Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {

// Get the first Feature from the successful geocoding response
                    CarmenFeature feature = results.get(0);

                  /*  IconFactory iconFactory = IconFactory.getInstance(Dashboard.this);
                    Icon icon = iconFactory.fromResource(R.drawable.mapbox_compass_icon);


                    mapboxMap.addMarker(new MarkerOptions()
                            .title(dangerZone.getDangersName())
                            .snippet(""+dangerZone.getId())
                            .position(new LatLng(dangerZone.getLatitude(), dangerZone.getLongitude())));*/

                    String urlstr = "https://shrinkcom.com/waff/admin/images/dangerzone/" + dangerZone.getImage();



                    Glide.with(activity).asBitmap().load(urlstr).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> transition) {

                            IconFactory iconFactory = IconFactory.getInstance(Dashboard.this);

                            Bitmap   bitmap = Bitmap.createScaledBitmap(bmp, 70, 70, true);
                            Icon icon = iconFactory.fromBitmap(bitmap);

                            //bmp.recycle();

                            mapboxMap.addMarker(new MarkerOptions()
                                    .title(dangerZone.getDangersName())
                                    .setSnippet("" + dangerZone.getId())
                                    .icon(icon)
                                    .position(new LatLng(dangerZone.getLatitude(), dangerZone.getLongitude())));

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);


                            mapboxMap.addMarker(new MarkerOptions()
                                    .title(dangerZone.getDangersName())
                                    .setSnippet("" + dangerZone.getId())

                                    .position(new LatLng(dangerZone.getLatitude(), dangerZone.getLongitude())));

                        }
                    });



                } else {


                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {

            }
        });


    }

    private void getRoute(LatLng destinationLatLong) {


        Point origin = Point.fromLngLat(sessionManager.getoriginAddress().getLongitude(), sessionManager.getoriginAddress().getLatitude());
        //Point destination = Point.fromLngLat(sessionManager.getDestinationAddresss().getLongitude(), sessionManager.getDestinationAddresss().getLatitude());


        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .destination(Point.fromLngLat(destinationLatLong.getLongitude() , destinationLatLong.getLatitude()))
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        if (response.body() == null) {

                            showMessage.showDialogMessage("No routes found, make sure you set the right user and access token.");

                            return;
                        } else if (response.body().routes().size() < 1) {

                            showMessage.showDialogMessage("No routes found.");

                            return;
                        }

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, dashboardLayoutBinding.waffMapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);

                        //addMarkerOnMap(origin.latitude() ,origin.longitude() , "" );
                        // addMarkerOnMap(destination.latitude() ,destination.longitude() , "" );

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    public void addMarkerOnMap() {


        try
        {
            for (int i = 0; i < dangerZoneArrayList.size(); i++) {

                DangerZone dangerZone = dangerZoneArrayList.get(i);


                IconFactory iconFactory = IconFactory.getInstance(Dashboard.this);
                Icon icon = iconFactory.fromResource(R.drawable.mapbox_compass_icon);

                double distance = distance(mCurrentLatLong.getLatitude(), mCurrentLatLong.getLongitude(), dangerZone.getLatitude(), dangerZone.getLongitude());

                boolean isOpenFeedBackDialog = false;


                String feedBackDangerId = "";


                if (!sessionManager.getUser().getUserId().equals(dangerZone.getUserId()))
                {
                    if (distance <= 0.1) {
                        if (dangerZone.getStatus() == 0) {
                            showNotification("You are in danger mode ");
                            dangerZone.setStatus(1);
                            sqliteDB.changeDangerStatus(dangerZone);
                        }
                    } else {


                        if (dangerZone.getStatus() == 1 && !isOpenFeedBackDialog) {

                            isOpenFeedBackDialog = true;
                            feedBackDangerId = "" + dangerZone.getId();
                            sendFeedBackOnIncident(feedBackDangerId);

                        }
                    }
                }




                String urlstr = "https://shrinkcom.com/waff/admin/images/dangerzone/" + dangerZone.getImage();



                Glide.with(this).asBitmap().load(urlstr).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> transition) {

                        IconFactory iconFactory = IconFactory.getInstance(Dashboard.this);

                        Bitmap   bitmap = Bitmap.createScaledBitmap(bmp, 70, 70, true);
                        Icon icon = iconFactory.fromBitmap(bitmap);

                        //bmp.recycle();

                        mapboxMap.addMarker(new MarkerOptions()
                                .title(dangerZone.getDangersName())
                                .setSnippet("" + dangerZone.getId())
                                .icon(icon)
                                .position(new LatLng(dangerZone.getLatitude(), dangerZone.getLongitude())));

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);


                        mapboxMap.addMarker(new MarkerOptions()
                                .title(dangerZone.getDangersName())
                                .setSnippet("" + dangerZone.getId())

                                .position(new LatLng(dangerZone.getLatitude(), dangerZone.getLongitude())));

                    }
                });



            }

        }
        catch (Exception e)
        {
         Log.e("msg" , e.getMessage());
        }



    }




    /**
     * Google api client methode for get current location
     **/

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        requestForPermissions();


    }

    public void requestForPermissions() {


        if (UserPermision.canGetLocationFromGps(this)) {
            UserPermision.requestForPermission(activity, permissionsToRequest, status -> {

                if (UserPermision.checkPermission(activity, permissionsToRequest)) {


                    startLocationUpdates();


                    Bitmap currentLocation = BitmapFactory.decodeResource(
                            Dashboard.this.getResources(), R.drawable.backcircle);


                    //addMarkerOnMap(location.getLatitude() ,location.getLongitude() ,currentLocation , "");


                } else {
                    requestForPermissions();
                }
            });
        } else {
            UserPermision.openActivityForGpsON(this, 1000);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1000) {

            if (mapboxMap != null) {
                if (mapboxMap.getStyle() != null) {

                    setUpMapBox(mapboxMap);

                }
            }


        }


        if (resultCode == Activity.RESULT_OK && requestCode == 2000) {

            if (data != null) {
                addDangerOnServer(dangerPoints, data.getStringExtra("danger_id"), data.getStringExtra("danger_name"));

            }


        }


        if (requestCode == OriginAddressRequest && resultCode == Activity.RESULT_OK) {

            drawablesidePanel();
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);

            Point point =  (Point) feature.geometry();
            sessionManager.saveOridinAddress(point.latitude(), point.longitude(), "" + feature.address());
            if (checkDrawPathEgibility()) {
                //getRoute();
            }


        }


        if (requestCode == 6000 && resultCode == Activity.RESULT_OK) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);

            Point point =  (Point) feature.geometry();
            try
            {
                String address = GeoCoderDataParser.getAddressFromLatitudeLongitude(activity, point.latitude(), point.longitude());
                dashboardLayoutBinding.gomydanger.setText(""+address);
            }
            catch (Exception e)
            {

            }





            if (checkDrawPathEgibility()) {

                 if (mCurrentLatLong != null)
                 {
                     setCurrentLocation(mCurrentLatLong.getLatitude() , mCurrentLatLong.getLongitude());

                 }
                getRoute(new LatLng(point.latitude() , point.longitude()));
            }


        }

        if (requestCode == DestinationAddressRequest && resultCode == Activity.RESULT_OK) {
            drawablesidePanel();
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);

           Point point =  (Point) feature.geometry();


            sessionManager.saveDestinationAddress(point.latitude(), point.longitude(), "" + feature.address());
            String address = GeoCoderDataParser.getAddressFromLatitudeLongitude(activity, point.latitude(), point.longitude());
            dashboardLayoutBinding.tvDestinationAddress.setText(""+address);

            if (checkDrawPathEgibility()) {



                getRoute(new LatLng(point.latitude(), point.longitude()));
            }


        }


        if (requestCode == 7000 && resultCode == Activity.RESULT_OK) {




            try {
                List<Marker> markerArrayList = mapboxMap.getMarkers();

                for (Marker marker : markerArrayList) {
                    marker.remove();
                }
            } catch (Exception e) {

            }

            getDangerZone();

        }





    }

    public boolean checkDrawPathEgibility() {
        WaffAddress originAddress = sessionManager.getoriginAddress();
        WaffAddress destinationAddress = sessionManager.getoriginAddress();


        if (originAddress.getLatitude() != 0.0 && originAddress.getLongitude() != 0.0 && !TextUtils.isEmpty(originAddress.getGoogleAddress())) {
            if (destinationAddress.getLatitude() != 0.0 && destinationAddress.getLongitude() != 0.0 && !TextUtils.isEmpty(destinationAddress.getGoogleAddress())) {
                return true;
            }
        }


        return false;

    }


    public void addDangerOnServer(LatLng latLng, String dangersId, String name) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("action", "addDangerZone");
        param.put("user_id", userData.getUserId());
        param.put("latitude", "" + latLng.getLatitude());
        param.put("longitude", "" + latLng.getLongitude());
        param.put("city", "");
        param.put("dangers_id", dangersId);

        okHttpRequest.getResponse(param, new ServerRespondingListener(this) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {

                String ids = "";

                try {
                    String dangerJsonArrayStr = resultData.getString("userData");
                    ArrayList<DangerZone> dangerZoneArrayList = new Gson().fromJson(dangerJsonArrayStr, new TypeToken<List<DangerZone>>() {
                    }.getType());

                    ids = "" + dangerZoneArrayList.get(0).getId();

                    sqliteDB.saveDangerList(dangerZoneArrayList);

                    addMarkerOnMap();
                    addMarkerOnMap(dangerZoneArrayList.get(0));


                } catch (Exception e1) {
                }



              //  addMarkerOnMap(latLng.getLatitude(), latLng.getLongitude(), name, ids);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        //location.setLatitude(22.7375);
       // location.setLongitude(75.8759);

        mCurrentLatLong = new LatLng(location.getLatitude(), location.getLongitude());


        //2

        try {
            List<Marker> markerArrayList = mapboxMap.getMarkers();

            for (Marker marker : markerArrayList) {
                marker.remove();
            }
        } catch (Exception e) {

        }
//////////////


        if (navigationMapRoute != null && currentRoute != null) {
            navigationMapRoute.addRoute(currentRoute);

        }


        handler.post(() -> getDangerZone());


    }

    public void startNewActivityForSelectAddress(int requestCode) {
        try {

           /* Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(activity);
            startActivityForResult(intent, requestCode);*/

            PlaceOptions.Builder builder = PlaceOptions.builder();

            builder.backgroundColor(getResources().getColor(R.color.white));

            PlaceOptions placeOptions =   builder.build() ;

            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(getString(R.string.accessToken))
                    .placeOptions(placeOptions)
                    .build(this);
            startActivityForResult(intent, requestCode);


        } catch (Exception e) {
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000 * 30);
        mLocationRequest.setFastestInterval(1000 * 30);
        if (UserPermision.checkPermission(this, permissionsToRequest)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);

        }


    }

    public void stopLocationUpdates() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    public void getDangerZone() {


        Dashboard.this.dangerZoneArrayList.clear();
        Dashboard.this.dangerZoneArrayList.addAll(sqliteDB.getDangerList());


        setCurrentLocation(mCurrentLatLong.getLatitude(), mCurrentLatLong.getLongitude());


        if (Dashboard.this.dangerZoneArrayList.size() == 0) {
            okHttpRequest.getResponseGet(WebServices.getDangerZone, new ServerRespondingListener(this) {

                public void onError(String msg) {

                }


                @Override
                public void onRespose(@NotNull JSONObject resultData) {


                    try {
                        String dangerJsonArrayStr = resultData.getString("userData");
                        ArrayList<DangerZone> dangerZoneArrayList = new Gson().fromJson(dangerJsonArrayStr, new TypeToken<List<DangerZone>>() {
                        }.getType());

                        Dashboard.this.dangerZoneArrayList.clear();
                        Dashboard.this.dangerZoneArrayList.addAll(dangerZoneArrayList);

                        addMarkerOnMap();


                    } catch (Exception e1) {
                    }


                }
            });
        } else {
            addMarkerOnMap();

        }


    }

    public void fireAlaramIntent() {
      /*  alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent11 = new Intent(Dashboard.this, SaveDataSqliteBroadcast.class);
        alaramPendingIntent = PendingIntent.getBroadcast(Dashboard.this, 7234677, intent11, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance(Locale.ENGLISH).getTimeInMillis() + 1000, 1000 * 60,
                alaramPendingIntent);*/


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent11 = new Intent(Dashboard.this, SaveDataSqliteBroadcast.class);
                sendBroadcast(intent11);


            }
        }, 1000, 10000);

    }

    public void stopAlaramIntent() {
        // alarmManager.cancel(alaramPendingIntent);

        timer.cancel();
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    //requestForFeedBack
    public void sendFeedBackOnIncident(String feedBackDangerId) {


        Intent intent = new Intent(getApplicationContext(), SendFeedBackActivity.class);
        intent.putExtra("feed_back_danger_id", feedBackDangerId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(intent);


        /*try {
            sendFeedBackDiloag.dismiss();
        } catch (Exception e) {

        }

        sendFeedBackDiloag = new Dialog(this);
        sendFeedBackDiloag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sendFeedBackDiloag.setContentView(R.layout.user_incident_feedback_layout);
        sendFeedBackDiloag.getWindow().setLayout(Validation.getDeviceWidth(this) - 50, LinearLayout.LayoutParams.WRAP_CONTENT);
        greenThumbBtn = sendFeedBackDiloag.findViewById(R.id.green_thumb_btn);
        redThumbBtn = sendFeedBackDiloag.findViewById(R.id.red_thumb_btn);

        feedbackBtn = sendFeedBackDiloag.findViewById(R.id.feedback_btn);
        commentEditTv = sendFeedBackDiloag.findViewById(R.id.comment_edit_tv);

        status = "1";

        greenThumbBtn.setOnClickListener(v -> {

            redThumbBtn.setImageResource(R.drawable.red_thumb_inactive);
            greenThumbBtn.setImageResource(R.drawable.green_thumb);
            status = "1";

        });


        redThumbBtn.setOnClickListener(v -> {

            redThumbBtn.setImageResource(R.drawable.red_thumb);
            greenThumbBtn.setImageResource(R.drawable.green_thumb_inactive);
            status = "0";
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sendFeedBackDiloag.dismiss();
                requestForFeedBack(commentEditTv.getText().toString(), feedBackDangerId, status);
            }
        });


        sendFeedBackDiloag.show();*/
    }


    //action=comment (user_id, comment, dangers_id)

    public void requestForCommentOnDanger(String comment, String dangerZoneId) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("action", "comment");
        param.put("comment", comment);
        param.put("dangers_id", dangerZoneId);

        okHttpRequest.getResponse(param, new ServerRespondingListener(this) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {


                try {
                    showMessage.showDialogMessage(resultData.getString("message"));

                } catch (Exception e) {
                    showMessage.showDialogMessage(e.getMessage());
                }

            }
        });

    }


    public void showNotification(String msg) {
        displayCustomNotificationForOrders(msg);
    }

    //action=feedback (, , dangers_id, status )
    public void requestForFeedBack(String comment, String dangersId, String status) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("action", "feedback");
        param.put("user_id", sessionManager.getUser().getUserId());
        param.put("comment", comment);
        param.put("dangers_id", dangersId);
        param.put("status", status);

        okHttpRequest.getResponse(param, new ServerRespondingListener(this) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {


                try {
                    showMessage.showDialogMessage(resultData.getString("message"));
                    sqliteDB.changeDangerStatus(dangersId, "" + 0);

                } catch (Exception e) {
                    showMessage.showDialogMessage(e.getMessage());
                }

                //

            }
        });

    }


    public void startNavigation() {
        try {
            PlaceOptions.Builder builder = PlaceOptions.builder();

            builder.backgroundColor(getResources().getColor(R.color.white));

            PlaceOptions placeOptions =   builder.build() ;


            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(getString(R.string.accessToken))
                    .placeOptions(placeOptions)
                    .build(this);
            startActivityForResult(intent, 6000);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please Select destination address", Toast.LENGTH_LONG).show();
        }


    }


    private NotificationChannel mChannel;
    private NotificationManager notifManager;


    private void displayCustomNotificationForOrders(String description) {


       /* NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.destination_pin_event_bar) // notification icon
                .setContentTitle(getString(R.string.app_name)) // title for notification
                .setContentText(msg) // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 54640, intent, 0);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());*/



        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            Intent intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", getString(R.string.app_name), importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(getNotificationIcon()) // required
                    .setContentText(description)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (getResources(), R.mipmap.ic_launcher))
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = builder.build();
            notifManager.notify((int) System.currentTimeMillis(), notification);
        } else {

            Intent intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;

            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(description)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(getString(R.string.app_name)).bigText(description));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.destination_pin_event_bar : R.drawable.destination_pin_event_bar;
    }

    public void goMyDanger()
    {
        Intent intent = new Intent(getApplicationContext() , MyDangerListActivity.class);
        startActivityForResult(intent , 7000);
    }



}