package br.com.agendasus.auth.v1.infrastructure.system;

import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component("dateUtils")
public class DateUtils {

    public static String getDiaMesAno(Calendar date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date.getTime());
        }
        return null;
    }

    public static String getMesAno(Calendar date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            return sdf.format(date.getTime());
        }
        return null;
    }

    public static Calendar getDiaMesAno(String time) throws ResponseException {
        try {
            if (time != null) {
                Calendar calendar = DateUtils.getCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(time));
                return zeraHorario(calendar);
            }
            return null;
        } catch (ParseException e) {
            throw new ResponseException("utils.date.formato.incorreto.data");
        }
    }

    public static Date getHora(String time) throws ResponseException {
        try {
            if (time != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                return sdf.parse(time);
            }
            return null;
        } catch (ParseException e) {
            throw new ResponseException("utils.date.formato.incorreto.hora");
        }
    }

    public static String getDiaMesAnoHoraMinutoSegundo(Calendar date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date.getTime());
        }
        return null;
    }

    public static Calendar getDiaMesAnoHoraMinutoSegundo(String time) throws ResponseException {
        try {
            if (time != null) {
                Calendar calendar = DateUtils.getCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                calendar.setTime(sdf.parse(time));
                return calendar;
            }
            return null;
        } catch (ParseException e) {
            throw new ResponseException("utils.date.formato.incorreto.data");
        }
    }


    public static Calendar zeraHorario(Calendar time) {
        Calendar tempo = (Calendar) time.clone();

        tempo.set(Calendar.HOUR_OF_DAY, 0);
        tempo.set(Calendar.MINUTE, 0);
        tempo.set(Calendar.SECOND, 0);
        tempo.set(Calendar.MILLISECOND, 0);

        return tempo;
    }

    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        Date dt = new Date();
        calendar.setTime(dt);
        return calendar;
    }

    public static String convertMilliToDaysHoursMinutesSeconds(Long milliseconds) {
        String time = "00:00";
        if(milliseconds > 0) {
            time = String.format("%2dd %02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toDays(milliseconds),
                    TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds)),
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
            );
        }
        return time;
    }

    public static Period periodBetween(Calendar dateStart, Calendar dateEnd) {
        LocalDate start = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return  Period.between(start, end);
    }

    public static Long periodBetweenInDays(Calendar dateStart, Calendar dateEnd) {
        LocalDate start = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(start, end);
    }

}
