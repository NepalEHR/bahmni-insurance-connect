package org.bahmni.insurance.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

public class InsuranceUtils {
	public static final java.lang.String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceUtils.class);

	/**
	 * This method configures Gson. We need to use workaround for null dates.
	 * 
	 * @return definitive null safe Gson instance
	 */
	public static Gson createDefaultGson() {
		// Trick to get the DefaultDateTypeAdatpter instance
		// Create a first Gson instance
		Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();

		// Get the date adapter
		TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);

		// Ensure the DateTypeAdapter is null safe
		TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();

		// Build the definitive safe Gson instance
		return new GsonBuilder().setDateFormat(DATE_FORMAT).registerTypeAdapter(Date.class, safeDateTypeAdapter)
				.create();
	}

	public static String mapToJson(Object obj) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

	}

	public static <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}
	public static <T> T mapFromXML(String xml, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(xml, clazz);
	}
	
	public static String convertBahmniDateStr(Date bahmniDate) {
		SimpleDateFormat inputPutdateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return inputPutdateFormat.format(bahmniDate);
	}

	
	public static Date convertBahmniDateToImis(String dateStr) {
		SimpleDateFormat inputPutdateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat outPutDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date returnDate = null;
		try {

			returnDate =  inputPutdateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		LocalDateTime newdate = LocalDateTime.parse(outStr, formatter);
		return newdate;*/
		return returnDate;
		
	}
}
