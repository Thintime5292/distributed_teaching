package com.zhp.teaching.application.controller.file;

import com.zhp.teaching.utils.FileUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @Class_Name FileController
 * @Author zhongping
 * @Date 2020/7/20 23:14
 **/
@Controller
@CrossOrigin
@RequestMapping("/file")
public class FileController {
    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> resultMap = new HashMap<>();
        String fileName = multipartFile.getOriginalFilename();
        String fileUrl = null;
        if (multipartFile != null)
            fileUrl = FileUploadUtil.upload(multipartFile);
        boolean success = fileUrl != null;
        Integer code = success ? 0 : 1;
        String msg = fileName + (success ? "上传成功!" : "上传失败!");
        resultMap.put("code", code);
        resultMap.put("msg", msg);
        resultMap.put("url", fileUrl);
        return resultMap;
    }
}
