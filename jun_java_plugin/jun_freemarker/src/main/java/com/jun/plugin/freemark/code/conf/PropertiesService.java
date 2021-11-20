package com.jun.plugin.freemark.code.conf;

/**
 * properties服务类
 */
public class PropertiesService {

	/**
	 * application配置文件
	 * 
	 * @return
	 */
	public static PropertiesConfig getApplicationConfig() {
		return PropertiesConfig.getInstance("/application.properties");
	}
}
