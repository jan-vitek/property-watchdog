package com.janvitek.propertywatchdog;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

class RequestTask extends AsyncTask<String, String, String>{

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
        	Log.d("RequestTask", "uri: " + uri[0]);
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            saveFailedUri(uri[0]);
        } catch (IOException e) {
            saveFailedUri(uri[0]);
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("RequestTask", "result: " + result);
    }
    
    public void saveFailedUri(String uri){
		String path = Environment.getExternalStorageDirectory() + "/PropertyWatchdog/" + "failedUri.txt";
		Log.d("RequestTask", "writing to " + path);
		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.println(uri);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}