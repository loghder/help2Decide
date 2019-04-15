package com.hebut.help2decide.LoginUI;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

;
/**
 * Created by cc on 2019/3/31 0031.
 */

//import com.alibaba.fastjson.JSON;
//import com.db.domain.Student;

/**
 * @author Carl
 */
public class vertifyCode {
	Map<String,String> map;
	public vertifyCode(){
		map= new HashMap();
	}

	public String send(String phone)throws Exception
	{
		map.put("phone",phone);
		String s = post(map,"http://39.105.221.212:8080/IDecisionCat/getVerificationCode");
		return s;
	}
	public String vertifyCode(String phone, String code)throws Exception
	{
		map.put("phone",phone);
		map.put("verifiyCode",code);
		String s = post(map,"http://39.105.221.212:8080/IDecisionCat/codeVerification");
		return s;
	}
	public class Vertify implements Callable<String> {      //有返回值的多线程内部类
		String phone;
		String code;
		String isTrue;
		public Vertify(String phone, String code){
			this.phone=phone;
			this.code=code;
		}
		@Override
		public String call() throws Exception {
			isTrue=vertifyCode(phone,code);
			return isTrue;
		}
	}
	// "http://localhost:8080/ComprehensiveEvaluation/teacher/getEvaluation"
	private static String post(Map<String,String> map, String u) throws Exception {
		
		StringBuffer params = new StringBuffer();
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	      Map.Entry<String, String> entry = it.next();
	      params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
	    }
		String substring = params.substring(0,params.length()-1);
		
		
		URL url = new URL(u);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");// 閿熺粨浜ゆā寮�
		conn.setDoOutput(true);// 閿熻鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
		conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		conn.setUseCaches(false);
		
		byte[] bypes = substring.getBytes();
		conn.getOutputStream().write(bypes);// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
		InputStream inStream = conn.getInputStream();
		byte[] bytes = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, len);
		}
		return new String(outStream.toByteArray(),"utf-8");
	}

	// http://192.168.31.248:8080/ComprehensiveEvaluation/student/getScore/161484
	private static String get(String url) throws Exception {
		URL u = new URL(url);

		HttpURLConnection httpUrlConn = (HttpURLConnection) u.openConnection();
		httpUrlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpUrlConn.setDoOutput(true);
		httpUrlConn.setDoInput(true);
		httpUrlConn.setUseCaches(false);
		httpUrlConn.setRequestMethod("GET");
		httpUrlConn.connect();

		InputStream in = httpUrlConn.getInputStream();
		InputStreamReader streamReader = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(streamReader);
		return reader.readLine();

	}


}
