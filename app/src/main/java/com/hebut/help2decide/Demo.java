package com.hebut.help2decide;

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

/**
 * @author Carl
 */
public class Demo {
    public static void main(String[] args) throws Exception {
//		 String s = get("http://localhost:8080/ComprehensiveEvaluation/student/161484");
//		 Student student = JSON.parseObject(s,Student.class);
//		 System.out.println(student.getName());
//		 System.out.println(student.getAge());
//		 String s = get("http://localhost:8080/ComprehensiveEvaluation/teacher/getEvaluation");
        Map<String,String> map = new HashMap();
//		map.put("phone", "13388001301");
//		map.put("verifiyCode","317930");
        map.put("str", "我是吃饭还是睡觉");
//		String s = post(map,"http://39.105.221.212:8080/IDecisionCat/getVerificationCode");
//		String s = post(map,"http://39.105.221.212:8080/IDecisionCat/codeVerification");
        String s = post(map,"http://39.105.221.212:8080/IDecisionCat/parseSentence");
        System.out.println(s);


//		Student stu = (Student) JSON.parseObject(s, Student.class);
//		java.util.List<Student> parseArray = JSON.parseArray(s, Student.class);
    }


    // "http://localhost:8080/ComprehensiveEvaluation/teacher/getEvaluation"
    public static String post(Map<String,String> map, String u) throws Exception {

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

    // get閿熸枻鎷烽敓鏂ゆ嫹
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
//		String s;
//		while ((s = reader.readLine()) != null) {
//		Student stu = (Student) JSON.parseObject(s, Student.class);
//		JSON.parseObject(s, List.class);
//		java.util.List<Student> parseArray = JSON.parseArray(s, Student.class);
//		System.out.println(stu.getClassid());
//		System.out.println(stu.getScore().getComprehensive());
//		}

    }


}
