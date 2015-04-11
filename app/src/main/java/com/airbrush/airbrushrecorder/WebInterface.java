package com.airbrush.airbrushrecorder;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.security.MessageDigest;
import java.util.zip.GZIPOutputStream;
//import java.net.CookieManager;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.util.InetAddressUtils;

import java.net.URLEncoder;

public class WebInterface
{
	private static String TAG = "WebInterface";
	private static String ADDRESS_FLIGHT = "http://airbrush.nucular-bacon.com/api/flights";
	private static String ADDRESS_USER = "http://airbrush.nucular-bacon.com/api/users";
	//private static String ADDRESS_SESSION = "http://airbrush.nucular-bacon.com:50000/api/session";
	//private HttpURLConnection _connection = null;
	
	private static String COOKIE_SESSION = "AirToken=";
	
	private Activity _activity = null;
	
	private String _httpResponse = "";
	
	public enum RequestType 
	{
		INVALID("INVALID"),
		GET("GET"),
		POST("POST");
		
		private RequestType(final String text)
		{
			_text = text;
		}
		
		private final String _text;
		
		@Override
		public String toString()
		{
			return _text;
		}
		
		public static RequestType fromString(String text)
		{
			if(text == GET.toString())
			{
				return GET;
			}
			else if(text == POST.toString())
			{
				return POST;
			}
			
			return INVALID;
		}
	}
	
	private class AsyncHttpRequest extends AsyncTask<String, Integer, String>
	{
		@Override
		protected String doInBackground(String...params)
		{
			String result = "";
			
			if(params.length <= 1)
				return "";
			
			RequestType type = RequestType.fromString(params[0]);
			
			if(type == RequestType.POST)
			{
				if(params.length == 4)
				{
					int r = postData(params[1], params[2], params[3]);
					result = "" + r;
				}
			}
			else if(type == RequestType.GET)
			{
				if(params.length == 3)
				{
					result = getData(params[1], params[2]);
				}
				else if(params.length == 2)
				{
					result = getData(params[1], null);
				}
			}
			
			return result;
		}
		
		public int postData(String address, String data, String cookie)
		{
			URL url;
			HttpURLConnection connection;
			
			int result = -1;
			
			//Log.d(TAG, address + " // " + data);
			
			try
			{
				url = new URL(address);
				
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Accept", "application/json");
				
				connection.setConnectTimeout(5000);
				
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
				if(cookie.length() > 0)
				{
					connection.setRequestProperty("Cookie", cookie);
				}
				
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				
				wr.write(data.getBytes());
				wr.flush();
				wr.close();
				
				result = connection.getResponseCode();
				
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer(); 
			    while((line = rd.readLine()) != null)
			    {
				    response.append(line);
				    response.append('\r');
			    }
			    rd.close();
			    
			    _httpResponse = response.toString();
			}
			catch(ClientProtocolException e)
			{
				Log.e(TAG + " postData ClientProtocolException", "Error: " + e);
			}
			catch(IOException e)
			{
				Log.e(TAG + " postData IOException", "Error: " + e);
			}
			catch(Exception e)
			{
				Log.e(TAG + " postData Exception", "Error: " + e);
			}
			
			return result;
		}
		
		public String getData(String address, String cookie)
		{
			URL url;
			HttpURLConnection connection;
			
			String result = "";
			
			try
			{
				url = new URL(address);
				
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Accept", "application/json");
				
				if(cookie != null && cookie.length() > 0)
				{
					connection.setRequestProperty("Cookie", cookie);
				}
				
				connection.setUseCaches(false);
				connection.setDoInput(true);
				//connection.setDoOutput(false);
				
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer(); 
			    while((line = rd.readLine()) != null)
			    {
				    response.append(line);
				    response.append('\r');
			    }
			    rd.close();
				
				_httpResponse = response.toString();
				result = _httpResponse;
			}
			catch(ClientProtocolException e)
			{
				Log.e(TAG, "Error: " + e);
			}
			catch(IOException e)
			{
				Log.e(TAG, "Error: " + e);
			}
			catch(Exception e)
			{
				Log.e(TAG, "Error: " + e);
			}
			
			return result;
		}
	}
	
	public WebInterface(Activity activity)
	{
		_activity = activity;
	}
	
	public String getHttpResponse()
	{
		return _httpResponse;
	}
	
	public int postFlight(Flight flight, String cookie)
	{
		//Log.d(TAG, "postFlight");
		
		try
		{
			if(wifiAvailable(_activity))
			{
				String response = new AsyncHttpRequest().execute(WebInterface.RequestType.POST.toString(), ADDRESS_FLIGHT, flight.serializeToHttp(), COOKIE_SESSION + cookie).get();
				
				int iResponse = Integer.parseInt(response);
				
				return iResponse;
			}
			else
			{
				Log.e(TAG, "Can't post flight, wifi is off");
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return -1;
	}
	
	public int createAccount(String name, String surname, String email, String password)
	{
		try
		{
			if(wifiAvailable(_activity))
			{
				String postData = "name=" + name + "&surname=" + surname + "&email=" + email + "&airhash=" + password;
				String response = new AsyncHttpRequest().execute(WebInterface.RequestType.POST.toString(), ADDRESS_USER, postData, "").get();
				
				int iResponse = Integer.parseInt(response);
				
				//TODO: return according to response
				return iResponse;
			}
			else
			{
				//DialogWifiOff dialog = new DialogWifiOff();
				//dialog.show(_activity.get, TAG);
				
				Log.e(TAG, "Can't create account, wifi is off");
				return -1;
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return -1;
	}
	
	public String login(String userId, String password)
	{
		String result = "";
		
		try
		{
			if(wifiAvailable(_activity))
			{
				String postData = "airhash=" + password;
				
				String address = ADDRESS_USER + "/" + userId + "/session";
				
				new AsyncHttpRequest().execute(RequestType.POST.toString(), address, postData, "").get();
				
				JSONObject object = new JSONObject(_httpResponse);
				result = object.getString("AirToken");
			}
			else
			{
				Log.e(TAG, "Can't login, wifi is off");
			}
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, e.toString());
		}
		catch (ExecutionException e)
		{
			Log.e(TAG, e.toString());
		}
		catch (JSONException e)
		{
			Log.e(TAG, e.toString());
		}
		
		return result;
	}
	
	public String requestUserId(String mailAddress, FragmentActivity activity)
	{
		String result = "";
		String response = "";
		
		try
		{
			if(wifiAvailable(_activity))
			{
				String address = ADDRESS_USER + "/" + URLEncoder.encode(mailAddress, "ISO-8859-1");
				
				response = new AsyncHttpRequest().execute(RequestType.GET.toString(), address).get();
				
				JSONObject jsonObject  = new JSONObject(response);
				result = jsonObject.getString("id");
			}
			else
			{
				Log.e(TAG, "Can't retrieve user id, wifi is off");
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return result;
	}
	
	//http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
	public static String getIPAddress(boolean useIPv4)
	{
        try
        {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces)
            {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                
                for (InetAddress addr : addrs)
                {
                    if (!addr.isLoopbackAddress())
                    {
                        String sAddr = addr.getHostAddress().toUpperCase(Locale.getDefault());
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4)
                        {
                            if (isIPv4) 
                                return sAddr;
                        }
                        else
                        {
                            if (!isIPv4)
                            {
                                int delim = sAddr.indexOf('%');
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
        	Log.e(TAG, ex.toString());
        }
        
        return "";
    }
	
	public static String toHash(String word)
	{
		String result = "";
		
		try
		{
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			String s = new String(word.getBytes(), "UTF-8");
			sha256.update(s.getBytes("UTF-8"));
			BigInteger hash = new BigInteger(1, sha256.digest());
			result = hash.toString(16);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return result;
	}
	
	public static String compress(String message)
	{
		String result = "";
		
		try
		{
			byte[] blockcopy = ByteBuffer.allocate(4).order(java.nio.ByteOrder.LITTLE_ENDIAN).putInt(message.length()).array();
			ByteArrayOutputStream os = new ByteArrayOutputStream(message.length());
			GZIPOutputStream gos = new GZIPOutputStream(os);
			gos.write(message.getBytes());
			gos.close();
			os.close();
			
			byte[] compressed = new byte[4 + os.toByteArray().length];
			System.arraycopy(blockcopy, 0, compressed, 0, 4);
			System.arraycopy(os.toByteArray(), 0, compressed, 4, os.toByteArray().length);
			
			return Base64.encodeToString(compressed, 0);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return result;
	}
	
	public static String saltPassword(String password)
	{
		String result = "";
		
		//handle short password, sort of...
		if(password.length() < 3)
		{
			return result;
		}
		
		result = password;
		
		int l = result.length();
		int hl = l/2;
		if(l%2 > 0)
		{
			hl += 1;
		}
		
		result = result.substring(0, 1) + 'A' + result.substring(1, hl) + 'I' + result.substring(hl, l-1) + 'R' + result.substring(l-1, l);
		
		return result;
	}
	
	public static Boolean wifiAvailable(Activity activity)
	{
		try
		{
			ConnectivityManager connManager = (ConnectivityManager)activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
			//NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			
			if(networkInfo != null && networkInfo.isConnected())
			{
				return true;
			}
			return false;
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return false;
	}
	
	public static Boolean validateMailAddress(String mailAddress)
	{
		return android.util.Patterns.EMAIL_ADDRESS.matcher(mailAddress).matches();
	}
}
