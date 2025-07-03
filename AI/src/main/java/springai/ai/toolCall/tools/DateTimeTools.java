package springai.ai.toolCall.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class DateTimeTools {
    // 注解添加工具
    @Tool(description = "获取用户时区的当前日期和时间")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "设置给定时间的用户闹钟，以 ISO-8601 格式提供")
    void setAlarm(String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Alarm set for " + alarmTime);
    }

    // 指定参数格式
    @Tool(description = "设置给定时间的用户闹钟")
    void setAlarm1(@ToolParam(description = "以 ISO-8601 格式提供") String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Alarm set for " + alarmTime);
    }
    // 编程方式构建 MethodToolCallback 将方法转变为工具。
    String getCurrentTime(){
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
