# log4j-slack-appender

```
<appender name="SLACK" class="edu.uwrf.eas.log4j.SlackAppender">
    <errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/>
    <param name="logURL" value="https://hooks.slack.com/services/WEBHOOK_URL"/>
    <param name="channel" value="#log4j"/>
    <param name="user" value="log4j"/>
    <param name="Threshold" value="ERROR"/>
    <layout class="org.apache.log4j.PatternLayout">
       <param name="ConversionPattern" value="%d{MMM dd, HH:mm} [%p] %c{1} - %m%n"/>
    </layout>
</appender>
 
<appender name="ASYNC" class="org.apache.log4j.AsyncAppender">
   <errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/>
   <appender-ref ref="SLACK"/>
</appender>
```
