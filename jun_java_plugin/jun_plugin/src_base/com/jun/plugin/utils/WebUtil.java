package com.jun.plugin.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.junit.Test;


/**
 * 
 * @author Administrator
 *
 */
public class WebUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("mmssSSS");
	/**
	 * ??????UUID
	 * @return
	 */
	public static String uuid(){
		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		return uuid;
	}
	/**
	 * ??????????????????
	 */
	public static String getNo(Integer hashCode){
		Date date = new Date();
		String string  = sdf.format(date);
		return hashCode+string;
	}
	public static String getDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	public static String getDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	

	/**
	 * ??????????????????IP??????
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "??????";
		}
		return ip;
	}
	
	
	

	public static String getArea(String strip){
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql;
		String strRtn = null;
		try{
//			MyJdbc myjdbc = new MyJdbc();
//			conn = myjdbc.getConn();
			sql = "select * from fullip where startip<='" + strip + "' and endip>='" + strip + "'";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				strRtn = rs.getString("country");
			}else{
				strRtn = "??????????";
			}
			rs.close();
			rs = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (pstmt != null)
				try{
					pstmt.close();
					pstmt = null;
				}catch(Exception e){}
			if (conn != null)
				try{
					conn.close();
					conn = null;
				}catch(Exception e){}
		}
		return strRtn;
	}
	/**
	 * ??????ip??????????????????????????????000.000.000.000
	 * @param ip
	 * @return ?????????????????ip
	 */
	public static String strfullip(String ip){
		StringBuffer buff = new StringBuffer();
		buff.append("");
		String strzero = "000";
		int ilen = 0;
		if(ip != null){
			String[] arrip = ip.split("\\.");
			if(arrip.length == 4){
				for(int i = 0; i < 4; i++){
					if (i==0){
						ilen = arrip[i].length();
						if(ilen < 3){
							buff.append(strzero.substring(0,3-ilen)).append(arrip[i]);
						}else{
							buff.append(arrip[i]);
						}
					}else{
						ilen = arrip[i].length();
						if(ilen < 3){
							buff.append(".").append(strzero.substring(0,3-ilen)).append(arrip[i]);
						}else{
							buff.append(".").append(arrip[i]);
						}
					}
				}
			}
		}
		return buff.toString();
	}
	/**
	 * @param args
	 */
	@Test
	public   void main() {
		String strip = "202.108.33.32";
		System.out.println(WebUtil.strfullip(strip));
		System.out.println(System.currentTimeMillis());
		System.out.println("ip" + strip + "????????????????????" + WebUtil.getArea(WebUtil.strfullip(strip)));
		System.out.println(System.currentTimeMillis());
	}

	
	
	/**
	 * ??????????????
	 */
	public static void deleteBuyCart(HttpServletRequest request){
		request.getSession().removeAttribute("buyCart");
	}
    /***
     * ????????URI??????????????,????????????????http://www.babasport.com/action/post.htm?method=add, ??????????????????"/action/post.htm"
     * @param request
     * @return
     */
    public static String getRequestURI(HttpServletRequest request){     
        return request.getRequestURI();
    }
    /**
     * ????????????????????????????????????????(?????????????????????????????????????????????????????)
     * @param request
     * @return
     */
    public static String getRequestURIWithParam(HttpServletRequest request){     
        return getRequestURI(request) + (request.getQueryString() == null ? "" : "?"+ request.getQueryString());
    }
    /**
     * ?????????cookie
     * @param response
     * @param name cookie???????????????
     * @param value cookie????????
     * @param maxAge cookie???????????????????(??????????????????????,??????????????????????????????,??????3*24*60*60; ?????????????0,cookie??????????????????????????????????????????????)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {        
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (maxAge>0) cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    
    /**
     * ????????cookie????????
     * @param request
     * @param name cookie???????????????
     * @return
     */
    public static String getCookieByName(HttpServletRequest request, String name) {
    	Map<String, Cookie> cookieMap = WebUtil.readCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie.getValue();
        }else{
            return null;
        }
    }
    
    protected static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (int i = 0; i < cookies.length; i++) {
                cookieMap.put(cookies[i].getName(), cookies[i]);
            }
        }
        return cookieMap;
    }
    /**
     * ????????html????????????
     * @param inputString
     * @return
     */
    public static String HtmltoText(String inputString) {
        String htmlStr = inputString; //??????html??????????????????????
        String textStr ="";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;          
        java.util.regex.Pattern p_ba;
        java.util.regex.Matcher m_ba;
        
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //????????????script??????????????????????????{??????<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //????????????style??????????????????????????{??????<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_html = "<[^>]+>"; //????????????HTML??????????????????????????????????
            String patternStr = "\\s+";
            
            p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //????????????script????????

            p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //????????????style????????
         
            p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //????????????html????????
            
            p_ba = Pattern.compile(patternStr,Pattern.CASE_INSENSITIVE);
            m_ba = p_ba.matcher(htmlStr);
            htmlStr = m_ba.replaceAll(""); //????????????????
         
         textStr = htmlStr;
         
        }catch(Exception e) {
                    System.err.println("Html2Text: " + e.getMessage());
        }          
        return textStr;//????????????????????????????
     }
    
    public static void main(String[] args) throws Exception {
		WebUtil p = new WebUtil();
		System.out.println("Web Class Path = " + p.getWebClassesPath());
		System.out.println("WEB-INF Path = " + p.getWebInfPath());
		System.out.println("WebRoot Path = " + p.getWebRoot());
	}

	public String getWebClassesPath() {
		String path = getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		return path;

	}

	public String getWebInfPath() throws IllegalAccessException {
		String path = getWebClassesPath();
		if (path.indexOf("WEB-INF") > 0) {
			path = path.substring(0, path.indexOf("WEB-INF") + 8);
		} else {
			throw new IllegalAccessException("??????????????????");
		}
		return path;
	}

	public String getWebRoot() throws IllegalAccessException {
		String path = getWebClassesPath();
		if (path.indexOf("WEB-INF") > 0) {
			path = path.substring(0, path.indexOf("WEB-INF/classes"));
		} else {
			throw new IllegalAccessException("??????????????????");
		}
		return path;
	}
	

    public static String getDateStr(String pt) {
        if (pt == null || pt.trim().length() == 0) {
            pt = "yyyy-MM-dd";
        }
        
        SimpleDateFormat fm = new SimpleDateFormat();
        fm.applyPattern(pt);
        return fm.format(new Date());
    }
    
    public static String getDateStr(Object dateObj, String pt) {
        Date date = null;
        if (dateObj instanceof Date) {
            if (dateObj == null) {
                return "";
            }
            date = (Date) dateObj;
        } else {
            if (dateObj == null || dateObj.toString().length() == 0) {
                return "";
            }
            
            java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(dateObj
                    .toString());
            date = new Date(sqlDate.getTime());
        }
        
        if (pt == null || pt.trim().length() == 0) {
            pt = "yyyy-MM-dd";
        }
        
        SimpleDateFormat fm = new SimpleDateFormat();
        fm.applyPattern(pt);
        return fm.format(date);
    }
    
    public static Date getDate(String str, String pt) throws ParseException {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        
        if (pt == null || pt.trim().length() == 0) {
            pt = "yyyy-MM-dd";
        }
        
        SimpleDateFormat fm = new SimpleDateFormat();
        fm.applyPattern(pt);
        return fm.parse(str);
    }
    
    public static java.sql.Timestamp getSqlDate() {
        long dateTime = new Date().getTime();
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(dateTime);
        return sqlDate;
    }
    
    public static java.sql.Timestamp getSqlDate(String timeValue) {
        java.sql.Timestamp sqlDate = null;
        Long dateTime = WebUtil.getDateTime(timeValue);
        if (dateTime != null) {
            sqlDate = new java.sql.Timestamp(dateTime.longValue());
        }
        return sqlDate;
    }
    
    public static Long getDateTime(String timeValue) {
        Long dateTime = null;
        
        if (timeValue != null && timeValue.trim().length() > 0) {
            timeValue = rightPadTo(timeValue, "1900-01-01 00:00:00");
            timeValue = timeValue.replace("/", "-");
            
            try {
                Date date = WebUtil
                        .getDate(timeValue, "yyyy-MM-dd HH:mm:ss");
                dateTime = new Long(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        return dateTime;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> parmsMap = new HashMap<String, String>();
        
        Map<String, String[]> properties = request.getParameterMap();
        Object obj = "";
        String value = "";
        String[] values = null;
        
        for (String key : properties.keySet()) {
            obj = properties.get(key);
            if (null == obj) {
                value = "";
            } else if (obj instanceof String[]) {
                value = "";
                values = (String[]) obj;
                for (int i = 0; i < values.length; i++) {
                    value += "," + values[i];
                }
                value = value.length() > 0 ? value.substring(1) : value;
            } else {
                value = obj.toString();
            }
            
            parmsMap.put(key, value);
        }
        
        return parmsMap;
    }
    
    
	public static String changeUTF(String str) {
		
		String newStr = null;
		try {
			newStr = new String(str.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newStr;
	}
    
    
    public static String rightPadTo(String src, String dec) {
        String retStr = src;
        int len = src.length();
        if (dec.length() - len > 0) {
            retStr += dec.substring(len);
        }
        return retStr;
    }
    /***********************************************************************************************/
    /***********************************************************************************************/

	public static <T> T requestToBean(HttpServletRequest request, Class<T> beanClass) {
		try {
			T bean = beanClass.newInstance();
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {// ?????????????????????????????????????????????
				String name = (String) e.nextElement();// ????????????????????????????????????
				String value = request.getParameter(name);// ??????request????????????????????????
				// ???????????????????????????????????????javaBean?????????????????????
				BeanUtils.setProperty(bean, name, value);
			}
			return bean;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	// ????????????????????????originBean???destinationBean??????
	public static void copyBean(Object src, Object dest) {
		ConvertUtils.register(new Converter() {// ??????Converter?????????convert??????????????????????????????
					public Object convert(Class type, Object value) {
						if (value == null)
							return null;
						String str = (String) value;
						if (str.trim().equals("")) {
							return null;
						}
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// ??????????????????
						try {
							return df.parse(str); // ??????????????????Date?????????
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}
				}, Date.class);
		try {
			BeanUtils.copyProperties(dest, src);// ??????????????????bean??????????????????
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	// ????????????????????????originBean???destinationBean??????
	public static void copyBean4(Object src, Object dest) {
		ConvertUtils.register(new Converter() {// ???????????????????????????
		    public Object convert(Class type, Object value) {
			if (value == null) return null;
			if (!(value instanceof String)) {
//			    throw new ConversionException("???????????????String?????????");
			}
			if (((String) value).trim().equals("")) {// trim:???????????????????????????????????????????????????????????????
			    return null;
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");// ??????????????????
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// ??????????????????
			try {
			    return df.parse((String) value); // ??????????????????Date?????????
			} catch (ParseException e) {
			    throw new RuntimeException(e);
			}
		    }
		}, java.util.Date.class);
//		ConvertUtils.register(new DateLocaleConverter(), java.util.Date.class);
		
		
		try {
			BeanUtils.copyProperties(dest, src);// ??????????????????bean??????????????????
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	// ????????????????????????originBean???destinationBean??????
	public static void copyBean3(Object src, Object dest) {
		ConvertUtils.register(new Converter() {// ???????????????????????????
			@SuppressWarnings("rawtypes")
			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				} else if (type == Timestamp.class) {
					return convertToDate(type, value, "yyyy-MM-dd HH:mm:ss");
				} else if (type == Date.class) {
					return convertToDate(type, value, "yyyy-MM-dd");
				} else if (type == String.class) {
					return convertToString(type, value);
				}else{
					throw new ConversionException("???????????? " + value.getClass().getName() + " ??? " + type.getName());
				}
			}
		}, java.util.Date.class);
//		ConvertUtils.register(new DateLocaleConverter(), java.util.Date.class);
//		BeanUtils.populate(b, map);
		
		try {
			BeanUtils.copyProperties(dest, src);// ??????????????????bean??????????????????
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	// ????????????????????????originBean???destinationBean??????
	public static void paramToBean(HttpServletRequest request, Object dest) {
		Object src=getAllParameters(request);
		ConvertUtils.register(new Converter() {// ???????????????????????????
			@SuppressWarnings("rawtypes")
			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				} else if (type == Timestamp.class) {
					return convertToDate(type, value, "yyyy-MM-dd HH:mm:ss");
				} else if (type == Date.class) {
					return convertToDate(type, value, "yyyy-MM-dd");
				} else if (type == String.class) {
					return convertToString(type, value);
				}else{
					throw new ConversionException("???????????? " + value.getClass().getName() + " ??? " + type.getName());
				}
			}
		}, java.util.Date.class);
//		ConvertUtils.register(new DateLocaleConverter(), java.util.Date.class);
//		BeanUtils.populate(b, map);
		
		try {
			BeanUtils.copyProperties(dest, src);// ??????????????????bean??????????????????
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	// ????????????????????????originBean???destinationBean??????
	@SuppressWarnings("rawtypes")
	public static void MapToBean(Map map, Object dest) {
		ConvertUtils.register(new Converter() {// ???????????????????????????
			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				} else if (type == Timestamp.class) {
					return convertToDate(type, value, "yyyy-MM-dd HH:mm:ss");
				} else if (type == Date.class) {
					return convertToDate(type, value, "yyyy-MM-dd");
				} else if (type == String.class) {
					return convertToString(type, value);
				}else{
					throw new ConversionException("???????????? " + value.getClass().getName() + " ??? " + type.getName());
				}
			}
		}, java.util.Date.class);
//		ConvertUtils.register(new DateLocaleConverter(), java.util.Date.class);
		
		try {
			BeanUtils.populate(dest, map);
 		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	// ?????????????????????ID???
	public static String gnerateID() {
		return UUID.randomUUID().toString();// UUID???????????????????????????
	}
	
	
	
	

	protected static Object convertToDate(Class type, Object value, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (value instanceof String) {
			try {
				if (StringUtil.isEmpty(value.toString())) {
					return null;
				}
				java.util.Date date = sdf.parse(String.valueOf(value));
				if (type.equals(Timestamp.class)) {
					return new Timestamp(date.getTime());
				}
				return date;
			} catch (Exception pe) {
				return null;
			}
		} else if (value instanceof Date) {
			return value;
		}
		throw new ConversionException("???????????? " + value.getClass().getName() + " ??? " + type.getName());
	}

	protected static Object convertToString(Class type, Object value) {
		if (value instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (value instanceof Timestamp) {
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			try {
				return sdf.format(value);
			} catch (Exception e) {
				throw new ConversionException("????????????????????????????????????");
			}
		} else {
			return value.toString();
		}
	}
	//************************************************************************************
	//************************************************************************************

	private static String webAppRoot = null;

	public static String getWebAppRoot() {
		return webAppRoot;
	}

	public static void setWebAppRoot(String pWebAppRoot) {
		webAppRoot = pWebAppRoot;
	}
	//************************************************************************************
	//************************************************************************************
	public static String removeHightlight(String content) {
		if (StringUtil.isEmpty(content))
			return content;
		int before = content.indexOf('<');
		int behind = content.indexOf('>');
		if (before != -1 || behind != -1) {
			behind += 1;
			content = content.substring(0, before).trim() + content.substring(behind, content.length()).trim();
			content = removeHightlight(content);
		}
		return content;
	}

 
	//************************************************************************************
	//************************************************************************************
	private static String numberFilePath;//number.txt???????????????
	static{
		URL url = WebUtil.class.getClassLoader().getResource("config.properties");
		numberFilePath = url.getPath();//?????????Tomcat????????????????????????????????????
	}
	/**
	 * ????????????????????????JavaBean?????????????????????????????????JavaBean??????????????????
	 * @param request
	 * @param clazz ????????????
	 * @return
	 */
	public static <T> T fillBean(HttpServletRequest request,Class<T> clazz){
		try {
			T bean = clazz.newInstance();
			//???????????????????????????:
			ConvertUtils.register(new DateLocaleConverter(), Date.class);
			BeanUtils.populate(bean, request.getParameterMap());
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	//???????????????????????????:yyyyMMdd00000001   20150210 00000001
	public synchronized static String genApplyNumber(){
		
		try {
			//??????number.txt??????????????????????????????
			InputStream in = new FileInputStream(numberFilePath);
			byte data[] = new byte[in.available()];
			in.read(data);
			in.close();
			String count = new String(data);//1
			//??????????????????????????????????????????
			//----------------------
			Date now = new Date();
			String prefix = new SimpleDateFormat("yyyyMMdd").format(now);//20150210
			//---------------------
			StringBuffer sb = new StringBuffer(prefix);
			for(int i=0;i<(8-count.length());i++){
				sb.append("0");
			}
			sb.append(count);
			//???1????????????number.txt??????
			OutputStream out = new FileOutputStream(numberFilePath);
			out.write((Integer.parseInt(count)+1+"").getBytes());
			out.close();
			
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static void main123(String[] args) {
		for(int i=0;i<10;i++){
			System.out.println(genApplyNumber());
		}
	}
	//************************************************************************************//
	//************************************************************************************//
    /***********************************************************************************************/
    /***********************************************************************************************/

	@Deprecated
	public static <T> T copyToBean_old(HttpServletRequest request, Class<T> clazz) {
		try {
			// ????????????????????????
			T t = clazz.newInstance();
			
			// ?????????????????????????????????????????
			Enumeration<String> enums = request.getParameterNames();
			// ????????????
			while (enums.hasMoreElements()) {
				// ???????????????????????????????:<input type="password" name="pwd"/>
				String name = enums.nextElement();  // pwd
				// ?????????????????????????????
				String value = request.getParameter(name);
				// ???????????????????????????????????????????????????????????????????
				BeanUtils.copyProperty(t, name, value);
			}
			
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * ???????????????????????????????????????
	 */
	public static <T> T copyToBean(HttpServletRequest request, Class<T> clazz) {
		try {
			// ??????????????????????????????????????????????????????????
			// ????????????????????????
			T t = clazz.newInstance();
			BeanUtils.populate(t, request.getParameterMap());
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/***********************************************************************************************/
	/***********************************************************************************************/

	public static <T> T requestToBean22(HttpServletRequest request, Class<T> beanClass) {
		try {
			T bean = beanClass.newInstance();
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {// ?????????????????????????????????????????????
				String name = (String) e.nextElement();// ????????????????????????????????????
				String value = request.getParameter(name);// ??????request????????????????????????
				// ???????????????????????????????????????javaBean?????????????????????
				BeanUtils.setProperty(bean, name, value);
			}
			return bean;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	// ????????????????????????originBean???destinationBean??????
	public static void copyBean22(Object src, Object dest) {
		ConvertUtils.register(new Converter() {// ??????Converter?????????convert??????????????????????????????
					public Object convert(Class type, Object value) {
						if (value == null)
							return null;
						String str = (String) value;
						if (str.trim().equals("")) {
							return null;
						}
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// ??????????????????
						try {
							return df.parse(str); // ??????????????????Date?????????
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}
				}, Date.class);
		try {
			BeanUtils.copyProperties(dest, src);// ??????????????????bean??????????????????
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	// ?????????????????????ID???
	public static String gnerateID22() {
		return UUID.randomUUID().toString();// UUID???????????????????????????
	}
	/***********************************************************************************************/
	/***********************************************************************************************/
	

	/**
	 * ??????????????????????????????. 
	 * return ?????????????????? ??????:windows,Linux,Unix???.
	 */
	public static String getOSName()
	{
		return System.getProperty("os.name").toLowerCase();
	}

	/**
	 * ??????Unix?????????mac??????.
	 * @return mac??????
	 */
	public static String getUnixMACAddress()
	{
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try
		{
			/**
			 * Unix????????????????????????eth0????????????????????? ????????????????????????mac????????????
			 */
			process = Runtime.getRuntime().exec("ifconfig eth0");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null)
			{
				/**
				 * ?????????????????????[hwaddr]
				 */
				index = line.toLowerCase().indexOf("hwaddr");
				/**
				 * ?????????
				 */
				if (index != -1)
				{
					/**
					 * ??????mac???????????????2?????????
					 */
					mac = line.substring(index + "hwaddr".length() + 1).trim();
					break;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (bufferedReader != null)
				{
					bufferedReader.close();
				}
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * ??????Linux?????????mac??????.
	 * 
	 * @return mac??????
	 */
	public static String getLinuxMACAddress()
	{
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try
		{
			/**
			 * linux????????????????????????eth0????????????????????? ????????????????????????mac????????????
			 */
			process = Runtime.getRuntime().exec("ifconfig eth0");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null)
			{
				index = line.toLowerCase().indexOf("????????????");
				/**
				 * ?????????
				 */
				if (index != -1)
				{
					/**
					 * ??????mac???????????????2?????????
					 */
					mac = line.substring(index + 4).trim();
					break;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (bufferedReader != null)
				{
					bufferedReader.close();
				}
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * ??????widnows?????????mac??????.
	 * 
	 * @return mac??????
	 */
	public static String getWindowsMACAddress()
	{
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try
		{
			/**
			 * windows???????????????????????????????????????mac????????????
			 */
			process = Runtime.getRuntime().exec("ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null)
			{
				/**
				 * ?????????????????????[physical address]
				 */
				index = line.toLowerCase().indexOf("physical address");
				if (index != -1)
				{
					index = line.indexOf(":");
					if (index != -1)
					{
						/**
						 * ??????mac???????????????2?????????
						 */
						mac = line.substring(index + 1).trim();
					}
					break;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (bufferedReader != null)
				{
					bufferedReader.close();
				}
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	
	//WindowsCmd ="cmd.exe /c echo %NUMBER_OF_PROCESSORS%";//windows?????????
	//SolarisCmd = {"/bin/sh", "-c", "/usr/sbin/psrinfo | wc -l"};  
	//AIXCmd = {"/bin/sh", "-c", "/usr/sbin/lsdev -Cc processor | wc -l"};  
	//HPUXCmd = {"/bin/sh", "-c", "echo \"map\" | /usr/sbin/cstm | grep CPU | wc -l "}; 
	//LinuxCmd = {"/bin/sh", "-c", "cat /proc/cpuinfo | grep ^process | wc -l"}; 

	/**
	 * ????????????main??????.
	 * 
	 * @param argc
	 *            ????????????.
	 */
	public static void main1(String[] argc )
	{
		String os = getOSName();
		System.out.println(os);
		if (os.startsWith("windows"))
		{
			String mac = getWindowsMACAddress();
			System.out.println("?????????windows:" + mac);
		} else if (os.startsWith("linux"))
		{
			String mac = getLinuxMACAddress();
			System.out.println("?????????Linux??????,MAC?????????:" + mac);
		} else
		{
			String mac = getUnixMACAddress();
			System.out.println("?????????Unix?????? MAC?????????:" + mac);
		}
	}

	
	/***********************************************************************************************/
	/***********************************************************************************************/


	/**
	 * ??????????????????IP??????
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr1(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.indexOf("0:") != -1) {
			ip = "??????";
		}
		return ip;
	}
	
	/**
	 * ??????????????????IP??????
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr2(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "??????";
		}
		return ip;
	}
	
	/***********************************************************************************************/
	/***********************************************************************************************/
	// ???????????????????????????cookie
	public static Cookie findCookieByName(Cookie[] cs, String name) {
		if (cs == null || cs.length == 0) {
			return null;
		}

		for (Cookie c : cs) {
			if (c.getName().equals(name)) {
				return c;
			}
		}

		return null;
	}
	/***********************************************************************************************/
	/***********************************************************************************************/
    /***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/




	
	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request) {
		String requestPath = request.getRequestURI() + "?" + request.getQueryString();
		if (requestPath.indexOf("&") > -1) {// ??????????????????
			requestPath = requestPath.substring(0, requestPath.indexOf("&"));
		}
		requestPath = requestPath.substring(request.getContextPath().length());// ??????????????????
		return requestPath;
	}

	public static Map getParams2(HttpServletRequest request)
	{
		Map params = new HashMap();
		String queryString = request.getQueryString();
		if (queryString != null && queryString.length() > 0)
		{
			String pairs[] = Pattern.compile("&").split(queryString);
			for (int i = 0; i < pairs.length; i++)
			{
				String p = pairs[i];
				int idx = p.indexOf('=');
				params.put(p.substring(0, idx), URLDecoder.decode(p.substring(idx + 1)));
			}

		}
		return params;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getAllParameters2(HttpServletRequest request)
	{
		Map bufferMap = Collections.synchronizedMap(new HashMap());
		try
		{
			for (Enumeration em = request.getParameterNames(); em.hasMoreElements();)
			{
				String name = (String)(String)em.nextElement();
				String values[] = request.getParameterValues(name);
				String temp[] = new String[values.length];
				if (values.length > 1)
				{
					for (int i = 0; i < values.length; i++)
						temp[i] = values[i];

					bufferMap.put(name, temp);
				} else
				{
					bufferMap.put(name, values[0]);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bufferMap;
	}

	public static String CharSetConvert2(String s, String charSetName, String defaultCharSetName)
	{
		String newString = null;
		try
		{
			newString = new String(s.getBytes(charSetName), defaultCharSetName);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException nulle)
		{
			nulle.printStackTrace();
		}
		return newString;
	}
	  //******************************************************************
	//******************************************************************
	  
	//******************************************************************
	//******************************************************************
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/


	//-- Content Type ?????? --//
	public static final String TEXT_TYPE = "text/plain";
	public static final String JSON_TYPE = "application/json";
	public static final String XML_TYPE = "text/xml";
	public static final String HTML_TYPE = "text/html";
	public static final String JS_TYPE = "text/javascript";
	public static final String EXCEL_TYPE = "application/vnd.ms-excel";

	//-- Header ?????? --//
	public static final String AUTHENTICATION_HEADER = "Authorization";

	//-- ?????????????????? --//
	public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

	/**
	 * ????????????????????????????????? Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		//Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
		//Http 1.1 header
		response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
	}

	/**
	 * ????????????????????????Header.
	 */
	public static void setNoCacheHeader(HttpServletResponse response) {
		//Http 1.0 header
		response.setDateHeader("Expires", 0);
		response.addHeader("Pragma", "no-cache");
		//Http 1.1 header
		response.setHeader("Cache-Control", "no-cache");
	}

	/**
	 * ??????LastModified Header.
	 */
	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}

	/**
	 * ??????Etag Header.
	 */
	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
	}

	/**
	 * ???????????????If-Modified-Since Header, ??????????????????????????????.
	 * 
	 * ???????????????, checkIfModify??????false ,??????304 not modify status.
	 * 
	 * @param lastModified ???????????????????????????.
	 */
	public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}

	/**
	 * ??????????????? If-None-Match Header, ??????Etag???????????????.
	 * 
	 * ??????Etag??????, checkIfNoneMatch??????false, ??????304 not modify status.
	 * 
	 * @param etag ?????????ETag.
	 */
	public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
		String headerValue = request.getHeader("If-None-Match");
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}

			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", etag);
				return false;
			}
		}
		return true;
	}

	/**
	 * ??????????????????????????????????????????Header.
	 * 
	 * @param fileName ?????????????????????.
	 */
	public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
		try {
			//?????????????????????
			String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
		} catch (UnsupportedEncodingException e) {
		}
	}

	/**
	 * ????????????????????????Request Parameters.
	 * 
	 * ???????????????Parameter??????????????????.
	 */
	@SuppressWarnings("unchecked")
	public static Map getParametersStartingWith(HttpServletRequest request, String prefix) {
		Enumeration paramNames = request.getParameterNames();
		Map params = new TreeMap();
		if (prefix == null) {
			prefix = "";
		}
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if (values == null || values.length == 0) {//NOSONAR
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		return params;
	}

	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	/***********************************************************************************************/
	
	


	
	public static void goTo(HttpServletRequest request, HttpServletResponse response, Object uri)
			throws ServletException, IOException {
		if (uri instanceof RequestDispatcher){
			((RequestDispatcher)uri).forward(request, response);
		} else if (uri instanceof String) {
			response.sendRedirect(request.getContextPath() + uri);
		} 
	}
	
	


	public static final String URL_FORM_ENCODED = "application/x-www-form-urlencoded";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
 

	public static Map getParams(HttpServletRequest request) {
		Map params = new HashMap();
		String queryString = request.getQueryString();
		if (queryString != null && queryString.length() > 0) {
			String pairs[] = Pattern.compile("&").split(queryString);
			for (int i = 0; i < pairs.length; i++) {
				String p = pairs[i];
				int idx = p.indexOf('=');
				params.put(p.substring(0, idx), URLDecoder.decode(p.substring(idx + 1)));
			}

		}
		return params;
	}

	@SuppressWarnings("unchecked")
	public static Map getAllParameters(HttpServletRequest request) {
		Map bufferMap = Collections.synchronizedMap(new HashMap());
		try {
			for (Enumeration em = request.getParameterNames(); em.hasMoreElements();) {
				String name = (String) (String) em.nextElement();
				String values[] = request.getParameterValues(name);
				String temp[] = new String[values.length];
				if (values.length > 1) {
					for (int i = 0; i < values.length; i++)
						temp[i] = values[i];

					bufferMap.put(name, temp);
				} else {
					bufferMap.put(name, values[0]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufferMap;
	}

	public static String CharSetConvert(String s, String charSetName, String defaultCharSetName) {
		String newString = null;
		try {
			newString = new String(s.getBytes(charSetName), defaultCharSetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NullPointerException nulle) {
			nulle.printStackTrace();
		}
		return newString;
	}
	

	
	/**
     * ??????????????URL????????????GET??????????????????????????????
     * @param url ???????????????????????????URL
     * @param param ?????????????????????
     * @return URL ?????????????????????????????????????????????????????????
     */
    public static String sendGet(String url, HashMap<String,String> params) {
        String result = "";
        BufferedReader in = null;
        try {
        	/**????????????????????**/
        	String param = parseParams(params);
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            /**??????????URL???????????????????????**/
            URLConnection connection = realUrl.openConnection();
            /**??????????????????????????????????????????????**/
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            /**??????????????????????????????????**/
            connection.connect();
            /**???????????? BufferedReader????????????????????????????????URL??????????????**/
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("????????????GET?????????????????????????????????" + e);
            e.printStackTrace();
        } finally {/**????????finally??????????????????????????????????????**/
            try {
                if(in != null) { in.close(); }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * ?????????????? URL ????????????POST??????????????????????????????
     * @param url ??????????????????????????? URL
     * @param param ?????????????????????
     * @return ?????????????????????????????????????????????????????????
     */
    public static String sendPost(String url, HashMap<String,String> params) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            /**??????????URL???????????????????????**/
            URLConnection conn = realUrl.openConnection();
            /**??????????????????????????????????????????????**/
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            /**????????????POST?????????????????????????????????????????????????????????**/
            conn.setDoOutput(true);
            conn.setDoInput(true);
            /**????????URLConnection??????????????????????????????????????**/
            out = new PrintWriter(conn.getOutputStream());
            /**?????????????????????????????????**/
            String param = parseParams(params);
            out.print(param);
            /**flush?????????????????????????????**/
            out.flush();
            /**????????????BufferedReader????????????????????????????????URL??????????????**/
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("???????????? POST ?????????????????????????????????"+e);
            e.printStackTrace();
        } finally{ /**????????finally???????????????????????????????????????????????????????????**/
            try{
                if(out!=null){   out.close();}
                if(in!=null){ in.close(); }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }   
    
    /**
     * ??????HashMap????????????????????????????????????????
     * @param map
     * @return
     */
	private static String parseParams(HashMap<String,String> map){
    	StringBuffer sb = new StringBuffer();
    	if(map != null){
	    	for (Entry<String, String> e : map.entrySet()) {
		    	sb.append(e.getKey());
		    	sb.append("=");
		    	sb.append(e.getValue());
		    	sb.append("&");
	    	}
	    	sb.substring(0, sb.length() - 1);
    	}
    	return sb.toString();
    }
	
	

	 
    private static class TrustAnyTrustManager implements X509TrustManager {
 
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
 
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
 
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }
 
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
 
    /**
     * post?????????????????????(https??????)
     * 
     * @param url
     *            ????????????
     * @param content
     *            ??????
     * @param charset
     *            ??????
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
    */
	public static String post(String url, String content, String charset) throws NoSuchAlgorithmException, KeyManagementException, IOException
	{
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
		
		URL console = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
		conn.setRequestProperty("ContentType", "text/xml;charset=utf-8");
		conn.setRequestProperty("charset", "utf-8");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		conn.connect();
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.write(content.getBytes(charset));
		out.flush();
		out.close();
		InputStream is = conn.getInputStream();
		if (is != null)
		{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1)
			{
				outStream.write(buffer, 0, len);
			}
			is.close();
			
			String result = "";
			
			result = new String(outStream.toByteArray(), "utf-8");
			return result;
		}
		return null;
	}
	
    public static void test(String[] args) throws Exception {
    	String url = "https://61.135.144.37:8443/userbinding/api/createpush";
    	//String url = "https://113.106.93.82:9401/api/stockapi_notice";
    	String content = "test"; 
    	String charset = "utf-8";
    	
    	String jsonString = post(url,content,charset);	//??????????????????
    	
    	System.out.println(jsonString);
    }
    
    
    

	public static String getString(HttpServletRequest request,String paramName)
	{
		String temp=request.getParameter(paramName);
		if(temp!=null&&!temp.equals(""))
			return temp;
		else
			return null;
	}
	public static String getString(HttpServletRequest request,String paramName,String defaultString){
		String temp=getString(request,paramName);
		if(temp==null)
			temp=defaultString;
		return temp;
	}
	public static int getInt(HttpServletRequest request,String paramName) throws NumberFormatException{
			return Integer.parseInt(getString(request,paramName));
	}
	public static int getInt(HttpServletRequest request,String paramName,int defaultInt){
		try{
			String temp=getString(request,paramName);
			if(temp==null)
				return defaultInt;
			else
				return Integer.parseInt(temp);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}



	
	


	public static String getParameter(HttpServletRequest request, String paramName) {
		return getParameter(request, paramName, false);
	}

	// ?????????????????????????????????
	public static String getParameter(HttpServletRequest request, String paramName, String defaultStr) {
		String temp = request.getParameter(paramName);
		if (temp != null) {
			if (temp.equals("")) {
				return defaultStr;
			} else {
				return nullToString(temp);
			}
		} else {
			return defaultStr;
		}

	}

	public static String getEscapeHTMLParameter(HttpServletRequest request, String paramName) {
		return nullToString(StringUtil.escapeHTMLTags(WebUtil.getParameter(request, paramName, true)));
	}

	public static String getParameter(HttpServletRequest request, String paramName, boolean emptyStringsOK) {
		String temp = request.getParameter(paramName);
		if (temp != null) {
			if (temp.equals("") && !emptyStringsOK) {
				return "";
			} else {
				return temp;
			}
		} else {
			return "";
		}
	}

	public static int getIntParameter(HttpServletRequest request, String paramName, int defaultNum) {
		String temp = request.getParameter(paramName);
		if (temp != null && !temp.equals("")) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static int getIntParameter(HttpServletRequest request, String paramName) {
		return getIntParameter(request, paramName, 0);
	}

	public static String nullToString(String oldString) {
		if (oldString == null) {
			return "";
		}
		return oldString;
	}

	public static String nullToString(String oldString, String defaultValue) {
		oldString = nullToString(oldString);
		if ("".equals(oldString)) {
			return defaultValue;
		}
		return oldString;
	}

	// ??????????????????????????
	public static String getRequestString(HttpServletRequest request, String s) {
		s = nullToString(s).trim();
		s = WebUtil.getEscapeHTMLParameter(request, s);
		s = StringUtil.toChinese(s);
		s = StringUtil.toUnicode(s);
		s = StringUtil.StringtoSql(s);
		return s;
	}


	public static String getSqlString(String s) {
		s = StringUtil.SqltoString(s);
		s = StringUtil.toChinese(s);
		s = nullToString(s).trim();
		return s;
	}

}
