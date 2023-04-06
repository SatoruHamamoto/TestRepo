package com.gnomes.common.tags;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TestCTagUtil {

  private static final String PATTERN_YYYYMMDDHHMmmss = "yyyy/MM/dd HH:mm:ss";
  private static final String PATTERN_YYYYMMDD = "yyyy/MM/dd";
  private static final String PATTERN_HHmmss = "HH:mm:ss";
  private static final String PATTERN_HHmm = "HH:mm";
  private static final String PATTERN_YYYYMMDDHHMmm = "yyyy/MM/dd HH:mm";
  private static final String PATTERN_YYYYMM = "yyyy/MM";

  public static Date createDate(int year, int month, int dayOfMonth, int hour, int minute,
      int second) {
    LocalDateTime datetime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    ZoneId zone = ZoneId.systemDefault();
    ZonedDateTime zonedDateTime = ZonedDateTime.of(datetime, zone);
    Instant instant = zonedDateTime.toInstant();
    return Date.from(instant);
  }

  public static String convertDateToString(int dataType, Date date) {
    String datePattern = null;
    switch (dataType) {
      case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss:
        datePattern = PATTERN_YYYYMMDDHHMmmss;
        break;
      case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDD:
        datePattern = PATTERN_YYYYMMDD;
        break;
      case GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmmss:
        datePattern = PATTERN_HHmmss;
        break;
      case GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmm:
        datePattern = PATTERN_HHmm;
        break;
      case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm:
        datePattern = PATTERN_YYYYMMDDHHMmm;
        break;
      case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMM:
        datePattern = PATTERN_YYYYMM;
        break;
    }
    return new SimpleDateFormat(datePattern).format(date);
  }

}
