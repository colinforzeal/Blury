package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import application.App;

public class NetworkConnector{
    public static boolean isConnected(){
        ConnectivityManager connManager = (ConnectivityManager)
                App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();

        return (info != null) && (info.isConnected());
    }

    public static InputStream  getInputStreamFromHttp (String urlAddress) throws IOException {
        InputStream inputStream = null;

        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            inputStream = connection.getInputStream();
        }
        catch (MalformedURLException e){
            Log.e(e.getClass().getName(), "In NetworkConnector.");
        }

        return inputStream;
    }

    public static InputStream getInputStreamFromHttps(String urlAddress) throws IOException{
        InputStream inputStream = null;

        try {
            URL url = new URL(urlAddress);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            inputStream = connection.getInputStream();
        }
        catch (MalformedURLException e){
            Log.e(e.getClass().getName(), "In NetworkConnector.");
        }

        return inputStream;
    }
}
