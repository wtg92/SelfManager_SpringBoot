package manager.util;

import static java.util.stream.Collectors.toList;
import static manager.system.SelfXParams.USER_TOKEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.booster.SecurityBooster;
import manager.system.SelfXErrors;

public abstract class UIUtil {

	public static List<Integer> transferToIntList(JSONArray jsonArray) {
		List<Integer> rlt = new ArrayList<>();

		for(int i=0;i<jsonArray.size();i++){
			rlt.add(jsonArray.getInteger(i));
		}

		return rlt;
	}


	public static long getLoginId(String auth) throws LogicException {
		try{
			return SecurityBooster.getUserId(auth.replace("Bearer ",""));
		}catch(Exception e){
			throw new LogicException(SelfXErrors.LOGIN_FAILED);
		}
	}

	public static int getParamIntegerOrZeroDefault(JSONObject param, String key){
		Integer val = param.getInteger(key);
		if(val == null){
			return 0;
		}
		return val;
	}

	public static double getParamDoubleOrZeroDefault(JSONObject param, String key){
		Double val = param.getDouble(key);
		if(val == null){
			return 0;
		}
		return val;
	}


	public static long getInDate(Long startDateUtc, String timezone) {
		return ZonedTimeUtils.copyDateOnly(startDateUtc,timezone);
	}

	/**
	 * ==========NEW VERSION=========
	 */


	public static String getParam(HttpServletRequest request, String key) throws NoSuchElement {
		String value = request.getParameter(key);
		if(value == null)
			throw new NoSuchElement();
		
		value = value.strip();
		return value;
	}

	public static String getNonNullParam(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParam(request, key);
		} catch (NoSuchElement e) {
			e.printStackTrace();
			throw new LogicException(SelfXErrors.REQUEST_ARG_NULL,key);
		}
	}
	



}
