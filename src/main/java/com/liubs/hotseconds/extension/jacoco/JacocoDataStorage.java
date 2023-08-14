package com.liubs.hotseconds.extension.jacoco;

import com.google.gson.reflect.TypeToken;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.util.IPUtil;
import com.liubs.hotseconds.extension.util.JsonUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author liubsyy
 * 这个类的作用是，热部署完重新部署前，将执行的jacoco数据保存下来，等下次部署的时候再读取出来和源码比对，生成新的覆盖率报告
 */
public class JacocoDataStorage implements IJacocoDataStorage {
    private static Logger logger = Logger.getLogger(JacocoDataStorage.class);

    private static JacocoDataStorage storage = new JacocoDataStorage();

    //热更新的类保存一下，勿删
    public static Map<String,Integer> freshClasses = new ConcurrentHashMap<>();

    private static volatile Thread thread = null;


    /**
     * 热更新一个类之后，会刷新覆盖率数据，插件内核层然后调用这个方法，勿删
     * @param className
     */
    public static void addFreshClass(String className){
        freshClasses.put(className,0);
        try{
            Class<?> aClass = Class.forName(className);
            Field jacocoDataField = aClass.getDeclaredField("$jacocoData");
            jacocoDataField.setAccessible(true);
            boolean[] jacocoData = (boolean[])jacocoDataField.get(null);
            jacocoDataField.setAccessible(false);
            if(null != jacocoData) {
                int count = 0;
                for(boolean e : jacocoData) {
                    if(e){
                        count++;
                    }
                }
                if(count >= 0) {
                    freshClasses.put(className,count);
                }
            }

        }catch (Throwable e) {
            e.printStackTrace();
        }


        //20s同步一次覆盖率（有变化时）
        if(null == thread) {
            synchronized (JacocoDataStorage.class) {
                if(null == thread) {
                    thread = new Thread(() -> {
                        while(true) {
                            try {
                                boolean needSync = false;
                                for(String classz : freshClasses.keySet()) {
                                    try{
                                        if (!classz.startsWith("com.liubs.")) {
                                            continue;
                                        }
                                        Class<?> aClass = Class.forName(classz);
                                        Field jacocoDataField = aClass.getDeclaredField("$jacocoData");
                                        jacocoDataField.setAccessible(true);
                                        boolean[] jacocoData = (boolean[])jacocoDataField.get(null);
                                        if(null == jacocoData) {
                                            continue;
                                        }
                                        jacocoDataField.setAccessible(false);

                                        int count = 0;
                                        for(boolean e : jacocoData) {
                                            if(e){
                                                count++;
                                            }
                                        }
                                        if(count<=0 || freshClasses.get(classz) == count) {
                                            continue;
                                        }

                                        needSync = true;
                                        freshClasses.put(classz,count);

                                        break;
                                    }catch (Throwable throwable) {
                                    }
                                }

                                if(needSync) {
                                    storage.storage();
                                }

                                Thread.sleep(20000);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        }

    }

    /**
     * 序列化热更新的类覆盖率到数据库
     * @return
     */
    @Override
    public boolean storage() {

        List<JacocoData> jacocoDataList = new ArrayList<>();
        for(String classz : freshClasses.keySet()) {
            if(!classz.startsWith("com.liubs.")) {
                continue;
            }

            try{
                Class<?> aClass = Class.forName(classz);
                Field jacocoDataField = aClass.getDeclaredField("$jacocoData");
                jacocoDataField.setAccessible(true);
                boolean[] jacocoData = (boolean[])jacocoDataField.get(null);
                jacocoDataField.setAccessible(false);

                JacocoData data = new JacocoData();
                data.setIp(IPUtil.getIpAddress());
                data.setProject(System.getenv("XX_Project"));
                data.setVersion(System.getenv("XX_Version"));
                data.setClassName(classz);
                data.setSize(jacocoData.length);
                data.setData(serializeClassData(jacocoData));

                jacocoDataList.add(data);
            }catch (Throwable e) {
                e.printStackTrace();
            }
        }


        try {
            String json = JsonUtils.toJson(jacocoDataList);
            logger.info("同步覆盖率到服务器:"+json);
            syncRemoteData(json,true);
        } catch (Throwable e) {
            e.printStackTrace();
        }



        return true;
    }

    private static String serializeClassData(boolean[] jacocoData){
        if(null == jacocoData || jacocoData.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(int i=0,len= jacocoData.length; i< len ;i++) {
            if(jacocoData[i]) {
                count++;
                sb.append(i);
                sb.append(",");
            }

        }
        if(count <= 0) {
            return null;
        }
        if(sb.charAt(sb.length()-1)==',') {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
    private static boolean[] unSerializeClassData(int size,String jacocoStr){
        if(size <= 0 || null == jacocoStr || jacocoStr.isEmpty()) {
            return null;
        }
        boolean[] jacocoData = new boolean[size];
        String[] split = jacocoStr.split(",");
        try{
            for(String e : split) {
                jacocoData[Integer.parseInt(e)] = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return jacocoData;
    }


    /**
     * 从数据库读取上一次热加载的类的覆盖率
     * @return
     */
    @Override
    public boolean readData() {

        new Thread() {
            @Override
            public void run() {

                try {
                    String jacocoDataJson = syncRemoteData(IPUtil.getIpAddress(), false);
                    logger.info("读取覆盖率:"+jacocoDataJson);
                    List<JacocoData> jacocoList = JsonUtils.parse(jacocoDataJson,new TypeToken<List<JacocoData>>(){}.getType());
                    for(JacocoData e : jacocoList) {
                        try {
                            Class<?> aClass = Class.forName(e.getClassName());

                            //这里不用$jacocoData字段，而是用$jacocoInit方法，不然读取到的数据可能为空
                            Method jacocoInit = aClass.getDeclaredMethod("$jacocoInit");
                            boolean isAccess = jacocoInit.isAccessible();
                            jacocoInit.setAccessible(true);
                            boolean[] jacocoData = (boolean[])jacocoInit.invoke(null);
                            jacocoInit.setAccessible(isAccess);

                            if(null != jacocoData && jacocoData.length == e.getSize()) {
                                boolean[] booleans = unSerializeClassData(e.getSize(), e.getData());
                                if(null != booleans) {
                                    for(int i = 0;i<jacocoData.length ;i++) {
                                        jacocoData[i] = booleans[i];
                                    }
                                    logger.info("读取覆盖率，class="+e.getClassName());
                                }
                            }
                        } catch (Throwable ex) {
                            logger.error("readData err,class="+e.getClassName(),ex);
                        }

                    }

                } catch (Throwable e) {
                    logger.error("readData err",e);
                }

            }
        }.start();

        return true;
    }


    private static String syncRemoteData(String message, boolean put) throws UnsupportedEncodingException {
        String urlString = "http://xx/helloswap/" + (put ? "jacocoDataPut" : "jacocoDataGet");

        String requestBody = "jacocoData="+URLEncoder.encode(message, "UTF-8");

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为POST
            connection.setRequestMethod("POST");

            connection.setReadTimeout(3000);

            // 启用输出流，以便我们可以写入请求体
            connection.setDoOutput(true);

            // 设置请求体的内容类型
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 写入请求体
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // 处理响应
               return response.toString();
            } else {
                System.out.println("POST request failed. Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
            System.out.println("An error occurred while sending the POST request.");
        }
        return "";
    }



    public static void main(String[] args)  {
        String ipAddress = IPUtil.getIpAddress();

        long time1 = System.currentTimeMillis();
        Random random = new Random();
        List<JacocoData> jacocoDataList = new ArrayList<>();
        for(int i=0;i<10;i++) {

            boolean[] jacocoData = new boolean[random.nextInt(1000) + 3000];
            for(int j=0;j<20;j++) {
                jacocoData[random.nextInt(20)]=true;
            }

            JacocoData data = new JacocoData();
            data.setIp(ipAddress);
            data.setProject("Test");
            data.setVersion("1.90.1");
            data.setClassName("com.liubs.Test");
            data.setSize(jacocoData.length);
            data.setData(serializeClassData(jacocoData));
            jacocoDataList.add(data);
        }


        try {
            syncRemoteData(JsonUtils.toJson(jacocoDataList),true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }


        System.out.println("serialize:"+(System.currentTimeMillis() - time1)+"ms");

        String jacocoJson = JsonUtils.toJson(jacocoDataList);
        long time2 = System.currentTimeMillis();

        System.out.println("jacocoJson: " +jacocoJson);


        String jacocoDataJson = null;
        try {
            jacocoDataJson = syncRemoteData(IPUtil.getIpAddress(), false);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        List<JacocoData> jacocoList = JsonUtils.parse(jacocoDataJson,new TypeToken<List<JacocoData>>(){}.getType());


        for(JacocoData e : jacocoList) {
            System.out.println("className:"+e.getClassName() + ",data="+ Arrays.toString(unSerializeClassData(e.getSize(), e.getData())));
        }

        System.out.println("unserialize:"+(System.currentTimeMillis() - time2)+"ms");


    }
}
