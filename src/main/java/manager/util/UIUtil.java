package manager.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.system.SelfXErrors;

import java.util.ArrayList;
import java.util.List;

public abstract class UIUtil {

	public static List<Integer> transferToIntList(JSONArray jsonArray) {
		List<Integer> rlt = new ArrayList<>();

		for(int i=0;i<jsonArray.size();i++){
			rlt.add(jsonArray.getInteger(i));
		}

		return rlt;
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

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }


    private static boolean isValidIp(String ip) {
        return ip != null
                && !ip.isBlank()
                && !"unknown".equalsIgnoreCase(ip)
                && !"127.0.0.1".equals(ip)
                && !"::1".equals(ip);
    }

}
