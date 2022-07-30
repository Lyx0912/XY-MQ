package com.xymq_cli.web.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 可视化界面主页
 * @author 黎勇炫
 * @date 2022年07月25日 21:46
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    /**
     * vue组件加载
     * @return
     */
    @RequestMapping("/component/{name}")
    public ResponseEntity<byte[]> component(@PathVariable String name,HttpServletRequest request, HttpServletResponse response){//throws Exception{
        ClassPathResource classPathResource = new ClassPathResource("templates/components/"+name);
        if (classPathResource.exists()) {
            try (InputStream inputStream = classPathResource.getInputStream()){
                byte[] bytes = IOUtils.toByteArray(inputStream);
                return downloadResponse(bytes, FilenameUtils.getName(name),request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static ResponseEntity<byte[]> downloadResponse(byte[] body, String fileName, HttpServletRequest request) {

        String header = request.getHeader("User-Agent").toUpperCase();
        HttpStatus status = HttpStatus.CREATED;
        try {
            //一般来说下载文件是使用201状态码的，但是IE浏览器不支持
            if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
                status = HttpStatus.OK;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(body.length);


        return new ResponseEntity<byte[]>(body, headers, status);
    }
}
