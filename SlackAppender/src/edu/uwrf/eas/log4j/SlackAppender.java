package edu.uwrf.eas.log4j;

/**
 *  Quick and dirty initial implementation of a Log4J appender for Slack
 *  Inspired by https://github.com/Tillerino/log4j-http-appender
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class SlackAppender extends AppenderSkeleton {

  final Logger                     log             = Logger.getLogger(SlackAppender.class);

  private final static String      GENERAL_CHANNEL = "#general";
  private final static String      DEFAULT_USER    = "log4j";
  private final static String      DEFAULT_ICON    = ":robot_face:";
  
  private String                   channel         = GENERAL_CHANNEL;
  private String                   user            = DEFAULT_USER;
  private String                   icon            = DEFAULT_ICON;
  private String                   logURL          = null;

  private ExecutorService          executorService = Executors.newCachedThreadPool();

  private static Map<Level,String> levelColors     = new HashMap<Level,String>() {

                                                     /**
     * 
     */

                                                    {
                                                       put(Level.DEBUG, "#439FE0");
                                                       put(Level.INFO, "good");
                                                       put(Level.WARN, "warning");
                                                       put(Level.ERROR, "danger");
                                                       put(Level.FATAL, "danger");
                                                     }
                                                   };

  public void close() {
    executorService.shutdown();
  }

  public boolean requiresLayout() {
    return true;
  }

  @Override
  protected void append(LoggingEvent paramLoggingEvent) {
    Layout layout = getLayout();
    String message = layout.format(paramLoggingEvent); // This is just the first
                                                       // line message, not the
                                                       // stack trace;
    StringBuffer buffer = new StringBuffer();
    String[] s = paramLoggingEvent.getThrowableStrRep();
    if(s != null) {
      for(int j = 0; j < s.length; j++) {
        buffer.append(s[j]);
        buffer.append(Layout.LINE_SEP);
      }
    }
    message += buffer.toString();

    String color = levelColors.containsKey(paramLoggingEvent.getLevel()) ? levelColors.get(paramLoggingEvent.getLevel()) : "good";
    String payload = "{\"channel\" : \"" + channel + "\", \"username\" : \"" + user + "\", \"icon_emoji\": \"" + icon + "\", \"attachments\" : [{ \"text\": \"" + message + "\", \"color\" : \""
        + color + "\"}]}";
    SlackThread httpThread = new SlackThread(logURL, payload);

    executorService.submit(httpThread);
  }

  public void setLogURL(String logURL) {
    this.logURL = logURL;
  }

  public void setChannel(String s) {
    this.channel = s;
  }

  public void setUser(String s) {
    this.user = s;
  }

  public void setIcon(String s) {
    this.icon = s;
  }

}
