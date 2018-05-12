package cn.focus.eco.house.zipkin.brave.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** 
 * @author Legend Han(leijunhan@sohu-inc.com) 
 * @date 2016-11-28 上午9:54:01 
 */
public class FileUtil {

	public static String fetchPropValue(String propertyName) {
		InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream("config/config.properties");
		Properties properties = new Properties();
		String value = "";
		try {
			properties.load(inputStream);
			value = properties.getProperty(propertyName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
}
