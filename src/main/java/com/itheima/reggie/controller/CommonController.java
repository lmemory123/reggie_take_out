package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * /common/download
 *
 * @author 尛猫
 * @version 1.0
 * @description: TODO
 * @date 2022/8/10 23:29
 */

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {


    @Value("${reggie.path}")
    private String FilePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upLoad(MultipartFile file) {
        System.out.println(Thread.currentThread().getId());
        String fileName = null;
        try {
            /*获取文件后缀*/
            String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            fileName = UUID.randomUUID().toString() + substring;
            log.info("文件后缀为" + substring);


            File files = new File(FilePath);
            /*判断目录是否存在*/
            if (!files.exists()) {
                /*不存在则重新创建*/
                files.mkdirs();
            }

            log.error(FilePath + fileName);
            file.transferTo(new File(FilePath + fileName));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 文件回显
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            /*输入流，通过输入流读取文件*/
            log.error(name);
            FileInputStream fileInputStream = new FileInputStream(FilePath + name);


            /*输出流，通过输出流将文件写回到浏览器，在浏览器展示图片*/
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            int let = 0;
            byte[] bytes = new byte[1024];
            while ((let = fileInputStream.read(bytes)) != -1) {
                /*将输入流转换到输出流*/
                outputStream.write(bytes, 0, let);
                /*刷新*/
                outputStream.flush();
            }
            /*关闭流*/
            outputStream.close();
            fileInputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
