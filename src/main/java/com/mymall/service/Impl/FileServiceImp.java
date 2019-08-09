package com.mymall.service.Impl;

import com.google.common.collect.Lists;
import com.mymall.service.IFileService;
import com.mymall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImp implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImp.class);
    //SpringMVc 封装的文件对象  MultipartFile
    public  String uplod(MultipartFile file,String path){
        String fileName=file.getName();
        //获取文件扩展名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uplodFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        System.out.println(uplodFileName);
        File filedir=new File(path);
        if(!filedir.exists()){
            filedir.setWritable(true);//设置文件可写权限
            filedir.mkdirs();//mkdirs()与mkdir()的区别前者再上传的时候a/b/c/d 只会上传d。
        }
        File targetFile =new File(filedir,uplodFileName);
        try {
            //用nginx 来做管理静态资源
            file.transferTo(targetFile);//文件上传到Tomcat成功——》下一步转发到ftp服务器并删除本地文件
            //上传到文件服务器
           // FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //删除本地
          //  targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常"+e);
            return null;
        }
     return targetFile.getName();
    }
}
