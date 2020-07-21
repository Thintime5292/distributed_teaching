package com.zhp.teaching.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Class_Name FileUploadUtil
 * @Author zhongping
 * @Date 2020/2/19 17:13
 **/
public class FileUploadUtil {
//    private static final String SERVER_HOST = "http://39.98.107.34";
    private static final String SERVER_HOST = "http://192.168.247.135";
/**
 * tracker.conf方式
    public static StorageClient getStorageClient() {
        String resource = "/tracker.conf";
        String trackerConf = FileUploadUtil.class.getResource(resource).getPath();
        TrackerClient trackerClient = null;
        StorageClient storageClient = null;
        try {
            ClientGlobal.init(trackerConf);
            trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            trackerServer.getConnection();
            storageClient = new StorageClient(trackerServer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storageClient;
    }
*/
    /**
     * tracker_client.properties方式
     * @return
     */
    public static StorageClient getStorageClient() {
        TrackerClient trackerClient = null;
        StorageClient storageClient = null;
        try {
            InputStream inputStream = FileUploadUtil.class.getResourceAsStream("/tracker_client.properties");
            Properties pps = new Properties();
            // 斜杠一定要，表示从当前本件的classpath开始读取
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                String[] str = line.split("=");
                pps.setProperty(str[0],str[1]);
            }
            ClientGlobal.initByProperties(pps);
            trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            trackerServer.getConnection();
            storageClient = new StorageClient(trackerServer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storageClient;
    }

    public static String upload(MultipartFile multipartFile) {
        StorageClient storageClient = getStorageClient();
        String fileUrl = SERVER_HOST;
        try {
            //获得上传文件的二进制对象
            byte[] multipartFileBytes = multipartFile.getBytes();
            //获取文件后缀名
            String filename = multipartFile.getOriginalFilename();
            String extName = filename.substring(filename.lastIndexOf(".") + 1);
            String[] uploadInfo = storageClient.upload_file(multipartFileBytes, extName, null);
            for (String s : uploadInfo) {
                fileUrl += "/" + s;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return fileUrl;
    }

    public static byte[] download(String fileUrl) {
        StorageClient storageClient = getStorageClient();
        byte[] bytes = null;
        fileUrl = fileUrl.substring(SERVER_HOST.length() + 1);
        String remote_filename = fileUrl.substring(fileUrl.indexOf("/") + 1);
        String group_name = fileUrl.substring(0, fileUrl.indexOf("/"));
        try {
            bytes = storageClient.download_file(group_name, remote_filename);
            ;
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (storageClient != null) {
                try {
                    storageClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Boolean delete(String fileUrl) {
        StorageClient storageClient = getStorageClient();
        try {
            fileUrl = fileUrl.substring(SERVER_HOST.length() + 1);
            String remote_filename = fileUrl.substring(fileUrl.indexOf("/") + 1);
            String group_name = fileUrl.substring(0, fileUrl.indexOf("/"));
            int result = storageClient.delete_file(group_name,remote_filename);
            if (result == 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (storageClient != null) {
                try {
                    storageClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
