package com.forcetech.android;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ForceTV
{
	private boolean p2pIsStart = false;
	private static String str;
	private static String force_nativeString = "http://127.0.0.1";
	private static int port = 9527;
	static
	{
		System.loadLibrary("forcetv");
	}

	public void initForceClient()
	{
		System.out.println("start Force P2P.........");
		try
		{
			BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("netstat").getInputStream()), 1024);
			while (true)
			{
				String str = localBufferedReader.readLine();
				if (str == null)
				{
					if (!this.p2pIsStart)
						Log.d("jni", String.valueOf(start(port, 20971520)));
					return;
				}
				if (str.contains("0.0.0.0:" + port))
					this.p2pIsStart = true;
			}
		} catch (Exception localException)
		{
			while (true)
				localException.printStackTrace();
		}
	}

	public native int start(int paramInt1, int paramInt2);

	public native int stop();

	/**
	 * request_switch_vod打开一个原力频道
	 * 
	 * @param serverString
	 *            服务器地址
	 * @param idString
	 *            频道ID
	 * @param hotlinkString
	 *            防盗链地址
	 * @return
	 */
	public static String request_switch_vod(String server, String id, String hotlink)
	{
		str = force_nativeString + ":" + port + "/cmd.xml?cmd=switch_chan&id=" + id + "&server=" + server + "&link=" + hotlink + "&userid=1";
		new Thread()
		{
			public void run()
			{
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				HttpGet localHttpGet = new HttpGet(str);
				try
				{
					HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpGet);
					Log.i("request_switch_vod:", localHttpResponse.getStatusLine().toString());
					return;
				} catch (Exception localException)
				{
					localException.printStackTrace();
				}
			}
		}.start();
		return force_nativeString + ":" + +port + "/" + id + ".ts";
	}

	/**
	 * request_stop_vod关闭所有原力频道
	 */
	public static void request_stop_vod()
	{
		new Thread()
		{
			public void run()
			{
				String str = force_nativeString + ":" + port + "/cmd.xml?cmd=stop_all_chan";
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				HttpGet localHttpGet = new HttpGet(str);
				try
				{
					HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpGet);
					Log.i("request_stop_vod:", localHttpResponse.getStatusLine().toString());
					return;
				} catch (Exception localException)
				{
					localException.printStackTrace();
				}
			}
		}.start();
	}
}
