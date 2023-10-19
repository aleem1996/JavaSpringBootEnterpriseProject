package com.gr.censusmanagement.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class Util {

	public static String dateOnlyPatternDashes = "MM-dd-yyyy";
	public static String DateOnlyPattern = "MM/dd/yyyy";
	private static String commDatePattern = "EEEE, MMMM dd, yyyy hh:mm:ss a";
	private static final int HOURS_IN_DAY = 24;
	private static final int MINUTES_IN_HOUR = 60;
	private static final int SECONDS_IN_MINUTE = 60;
	private static final int MILLI_SECONDS_IN_SECOND = 1000;

	public static String formatDateOnlyDashes(Date date) {
		SimpleDateFormat customFormat = new SimpleDateFormat(dateOnlyPatternDashes);
		customFormat.setLenient(false);
		return customFormat.format(date);
	}

	public static Date formatDate(String date) {
		if (date.isEmpty() || isNull(date)) {
			return null;
		}
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {

			Date formattedDate = outputFormat.parse(date);
			return formattedDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDateFromString(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date formattedDate = null;
        try {
        	formattedDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Util.isNotNull(formattedDate)) {
        	return outputFormat.format(formattedDate);        	
        } else {
        	return null;
        }
	}
	
	
	public static Date getCurrentDate() {
	    LocalDate currentDate = LocalDate.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	    return formatDate(currentDate.format(formatter));
	}

	public static Integer getDaysBetween(Date currentDate, Date previousDate, Boolean considerTimePart) {
		if (!considerTimePart) {
			currentDate = parseDateInGivenPattern(currentDate, DateOnlyPattern);
			previousDate = parseDateInGivenPattern(previousDate, DateOnlyPattern);
		}
		return (int) ((currentDate.getTime() - previousDate.getTime()) / (MILLI_SECONDS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY));
	}

	public static Date parseDateInGivenPattern(Date date, String format) {
		try {
			DateFormat sdf = new SimpleDateFormat(format);
			date = (Date) sdf.parse(sdf.format(date));
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return date;
	}

	public static String formatDate(Date date, String datePattern) {
		SimpleDateFormat customFormat = new SimpleDateFormat(datePattern);
		customFormat.setLenient(false);
		return customFormat.format(date);
	}

	/**
	 * This method formats date in general form i.e. EE, MMM dd, yyyy, hh:mm a.
	 * 
	 * @param date Specifies date to be formatted.
	 * @return Date formatted in general form i.e. EE, MMM dd, yyyy, hh:mm a.
	 */
	public static String getCommDetailFormatedDateTime(Date date) {
		SimpleDateFormat customFormat = new SimpleDateFormat(commDatePattern);
		customFormat.setLenient(false);
		return customFormat.format(date);
	}
	
	public static String parseStringDate(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	public static boolean isNullOrEmpty(String string) {
		return (((string == null) || (string.trim().length() == 0)) ? true : false);
	}

	public static boolean isNotNullAndEmpty(List<?> list) {
		return (((list != null) && (list.size() > 0)) ? true : false);
	}

	public static boolean isNotNullAndEmpty(String string) {
		return (((string != null) && (string.trim().length() > 0)) ? true : false);		
	}
	
	public static boolean isNullOrEmpty(List<?> list) {
		return (((list == null) || (list.size() == 0)) ? true : false);
	}

	public static boolean isNull(Object object) {
		return object == null;
	}

	public static boolean isNotNull(Object object) {
		return object != null;
	}

	public static boolean isNullOrEmpty(Object object) {
		return object == null;
	}

	public static boolean maxLength(String value, int max) {
		return (value.length() <= max);
	}
}
