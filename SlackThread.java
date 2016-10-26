package edu.uwrf.eas.log4j;

/**
 *  Quick and dirty initial implementation of a Log4J appender for Slack
 *  Inspired by https://github.com/Tillerino/log4j-http-appender
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

/**
 * 
 */
public class SlackThread implements Runnable {

	final Logger log = Logger.getLogger(SlackThread.class);

	private String url = "";
	private String payload = "";

	public SlackThread(String url, String payload) {
		this.url = url;
		this.payload = payload;
	}

	public void run() {
    byte[] p = payload.getBytes(StandardCharsets.UTF_8);
    HttpURLConnection connection = null;
    try {
      URL surl = new URL(this.url);
      connection = (HttpURLConnection)surl.openConnection();

      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("charset", "utf-8");
      connection.setRequestProperty("Content-Length", Integer.toString(p.length));
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Content-Language", "en-US");
      connection.setUseCaches(false);
      connection.setConnectTimeout(3000);

      try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream());) {
        wr.write(p);
      }
      catch(IOException e) {
        log.warn("IO Exception occurred in SlackThread.java", e);
      }
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      while((line = rd.readLine()) != null) {}
      rd.close();
    }
    catch(IOException e1) {
      log.warn("IO Exception occurred in SlackThread.java", e1);
    }
    finally {
      connection.disconnect();
    }
  }
}
