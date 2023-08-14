package com.liubs.hotseconds.extension.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class JsonUtils {

    /**
     * 默认的 {@code JSON} 日期/时间字段的格式化模式。
     */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final Gson GSON = createGson(true);
    private static final Gson GSON_NO_NULLS = createGson(false);

    /**
     * Create the standard {@link Gson} configuration
     *
     * @return created gson, never null
     */
    public static final Gson createGson() {
        return createGson(true);
    }

    /**
     * Create the standard {@link Gson} configuration
     *
     * @param serializeNulls whether nulls should be serialized
     * @return created gson, never null
     */
    public static final Gson createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(Record.class, new RecordAdapter());
//        builder.registerTypeHierarchyAdapter(Model.class, new ModelAdapter());
        builder.registerTypeAdapter(Date.class, new DateSerializer())
               .registerTypeAdapter(Date.class, new DateDeserializer());
        builder.setDateFormat(DEFAULT_DATE_PATTERN);//设置JSON格式化的日期格式
        //builder.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES);
//        builder.excludeFieldsWithoutExposeAnnotation();
        if (serializeNulls) {
            builder.serializeNulls();
        }
        return builder.create();
    }

    public static final Gson getGson() {
        return GSON;
    }

    public static final Gson getGson(final boolean serializeNulls) {
        return serializeNulls ? GSON : GSON_NO_NULLS;
    }

    public static final String toJson(final Object object) {
        return toJson(object, false);
    }

    public static final String toJson(final Object object, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(object) : GSON_NO_NULLS.toJson(object);
    }

    public static String toJson(Object target, String datePattern) {
        final GsonBuilder builder = new GsonBuilder();
        if (datePattern == null || datePattern.length() <= 0) {
            datePattern = DEFAULT_DATE_PATTERN;
        }
        builder.setDateFormat(datePattern);
        return builder.create().toJson(target);
    }

    public static String toJsonString(Object object){
        return toJson(object);
    }


    public static Map<String,Object> json2map(String json){
        Type type=new TypeToken<Map<String,Object>>(){}.getType();
        return parse(json, type);
    }

    public static String toJson(Object target, Type targetType) {
        return GSON_NO_NULLS.toJson(target, targetType);
    }

    public static final <V> V parse(String json, Class<V> type) {
        return GSON.fromJson(json, type);
    }

    public static final <V> V parse(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static final <V> V parse(Reader reader, Class<V> type) {
        return GSON.fromJson(reader, type);
    }

    public static final <V> V parse(Reader reader, Type type) {
        return GSON.fromJson(reader, type);
    }


	static class DateSerializer implements JsonSerializer<Date> {
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getTime());
		}
	}

	static class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					return new Date(json.getAsJsonPrimitive().getAsLong());
				} else if (jsonPrimitive.isString()) {
                    SimpleDateFormat format = new SimpleDateFormat(JsonUtils.DEFAULT_DATE_PATTERN);
                    String dateStr = json.getAsString();
					try {
						return format.parse(dateStr);
					} catch (ParseException e) {
					}
                    return null;
                }
            }
            return null;
        }
    }
}
