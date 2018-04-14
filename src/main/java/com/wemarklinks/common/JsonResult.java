package com.wemarklinks.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

public class JsonResult {

	public static Map<String, Object> RetJsonPage(int code, String msg, Object data, String[] field) {

		Map<String, Object> RetMap = new HashMap<>();
		RetMap.put("code", code);
		RetMap.put("msg", msg);
		if (null == data) {
			RetMap.put("data", "");
		} else {
			RetMap.put("data", FilterMap(data, field));
		}

		return RetMap;

	}

	private static Map<String, Object> FilterMap(Object data, String[] field) {

		String[] arr = { "pageNum", "pageSize", "total", "list", "pages" };

		Map<String, Object> RetMap = new HashMap<>();
		Map<Object, Object> datamap = new BeanMap(data);
		if (null == field || field.length == 0) {
			field = arr;
		}

		for (int i = 0; i < arr.length; i++) {
			String key = arr[i];
			RetMap.put(key, datamap.get(key));
		}
		return RetMap;

	}

	public static Map<String, Object> RetJsone(int code, String msg, Object data) {

		Map<String, Object> RetMap = new HashMap<>();
		RetMap.put("code", code);
		RetMap.put("msg", msg);
		RetMap.put("data", data);

		return RetMap;

	}

}
