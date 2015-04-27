package com.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonMarshaller {
	private static final JsonParser parser = new JsonParser();

	public static JsonObject parseJson(String postedData) {
		JsonElement element = parser.parse(postedData);
		if (element instanceof JsonObject) {
			return element.getAsJsonObject();
		}
		return null;
	}
}
