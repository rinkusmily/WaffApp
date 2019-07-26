package shrinkcom.waff.com.serverconntion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.interfaces.ServerRespondingListenerWithoutMessage;
import shrinkcom.waff.com.util.ShowMessage;
import shrinkcom.waff.com.util.Validation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OkHttpRequest
{
    Activity activity ;
    Request request ;
    OkHttpClient okHttpClient ;
    ShowMessage showMessage ;
    ProgressDialog progressDialog ;
    public OkHttpRequest( Activity activity)
    {
        this.activity = activity;
        okHttpClient = new OkHttpClient();
        showMessage = new ShowMessage(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    public OkHttpRequest()
    {
        okHttpClient = new OkHttpClient();
    }


    public void getResponse(HashMap<String , Object> param , ServerRespondingListener serverRespondingListener)
    {

        param.put("test" , "user");


        if (!Validation.isNetworkAvailable(activity))
        {
            serverRespondingListener.onError(activity.getString(R.string.network_error_message));

            return;

        }


        progressDialog.show();
        request = new Request.Builder()

                .url(WebServices.BASE_URL)

                .post(getMultiPartRrquest(param))
                .build();

        /*HttpUrl.Builder httpBuider = HttpUrl.parse(WebServices.BASE_URL).newBuilder();
        httpBuider.addQueryParameter("action", "regi.setType(MultipartBody.FORM)ster");


         request = new Request.Builder().url(httpBuider.build()).build();*/


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverRespondingListener.onError(e.getMessage());

                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();


                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        try
                        {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getInt("result") == 1)
                            {
                                serverRespondingListener.onRespose(jsonObject);

                            }
                            else
                            {
                                serverRespondingListener.onError(jsonObject.getString("message"));

                            }


                        }
                        catch (Exception e)
                        {
                            serverRespondingListener.onError(e.getMessage());

                        }


                    }
                });


                progressDialog.dismiss();
            }
        });
    }

    public void getResponseGet(String webUrl  ,ServerRespondingListener serverRespondingListener)
    {

        if (!Validation.isNetworkAvailable(activity))
        {
            serverRespondingListener.onError(activity.getString(R.string.network_error_message));

            return;

        }


        progressDialog.show();
        request = new Request.Builder()
                .url(webUrl)
                .get()
                .build();




        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverRespondingListener.onError(e.getMessage());

                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();


                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        try
                        {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getInt("result") == 1)
                            {
                                serverRespondingListener.onRespose(jsonObject);

                            }
                            else
                            {
                                serverRespondingListener.onError(jsonObject.getString("message"));

                            }


                        }
                        catch (Exception e)
                        {
                            serverRespondingListener.onError(e.getMessage());

                        }


                    }
                });


                progressDialog.dismiss();
            }
        });
    }

    private MultipartBody  getMultiPartRrquest(HashMap<String , Object> param)
    {
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);




        Set<Map.Entry<String, Object>> keyValuePairList = param.entrySet();

        Iterator<Map.Entry<String,Object>> keyValuePair = keyValuePairList.iterator();

        while (keyValuePair.hasNext())
        {
            Map.Entry<String,Object> keyValue = keyValuePair.next();
            final MediaType MEDIA_TYPE_PNG = MediaType.get("image/*");

            if (keyValue.getValue() instanceof File)
            {
                File file = (File) keyValue.getValue();
                multipartBody.addFormDataPart(keyValue.getKey() ,file.getName() ,RequestBody.create(MEDIA_TYPE_PNG, file));
            }
            else
            {
                multipartBody.addFormDataPart(keyValue.getKey() , keyValue.getValue().toString());
            }
        }





        return  multipartBody.build() ;
    }

    public void getResponseWithoutProgress(HashMap<String , Object> param , ServerRespondingListenerWithoutMessage serverRespondingListener)
    {

        param.put("test" , "user");

        request = new Request.Builder()

                .url(WebServices.BASE_URL)

                .post(getMultiPartRrquest(param))
                .build();

        /*HttpUrl.Builder httpBuider = HttpUrl.parse(WebServices.BASE_URL).newBuilder();
        httpBuider.addQueryParameter("action", "regi.setType(MultipartBody.FORM)ster");


         request = new Request.Builder().url(httpBuider.build()).build();*/


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {




            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();


                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        try
                        {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getInt("result") == 1)
                            {
                                serverRespondingListener.onRespose(jsonObject);

                            }



                        }
                        catch (Exception e)
                        {

                        }


                    }
                });


            }
        });
    }







    public void getResponseGet(String webUrl  ,ServerRespondingListenerWithoutMessage serverRespondingListener)
    {




        request = new Request.Builder()
                .url(webUrl)
                .get()
                .build();




        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {



            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();


                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        try
                        {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getInt("result") == 1)
                            {
                                serverRespondingListener.onRespose(jsonObject);

                            }



                        }
                        catch (Exception e)
                        {

                        }


                    }
                });


            }
        });
    }

}
