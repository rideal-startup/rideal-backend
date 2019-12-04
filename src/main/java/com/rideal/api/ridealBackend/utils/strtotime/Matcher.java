package com.rideal.api.ridealBackend.utils.strtotime;

import java.util.Date;

public interface Matcher {

	/**
	 * @param input
	 * @param refDateStr
	 * 
	 * @return the converted Date
	 */
	public abstract Date tryConvert(String input, String refDateStr);

}