package com.yuyang.template.util;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class FileUploadUtil {
    /**
     * 图片所在本地文件夹
     */
    @Value("${file.uploadFolder}")
    private String uploadFolder;

    /**
     * 图片项目路径
     */
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    /**
     * 获取文件项目相对路径
     * @return
     */
    public String getImgPath(String path){
        String realPath = uploadFolder.replaceAll("/","\\\\");
        String projectPath = staticAccessPath.replaceAll("/","\\\\");
        path = path.replace(realPath, "\\");
        path= projectPath.replaceAll("\\*","")+path;
        return path;
    }

    /**
     * 获取文件绝对路径
     */
    public String getRealPath(String path){
        String projectPath = staticAccessPath.replaceAll("/","\\\\");
        projectPath = projectPath.replaceAll("\\*","");
        path = uploadFolder+path.replace(projectPath,"");
        return path;
    }

    /**
     * 按日期打散目录
     */
    public File makeFullDirectoryObject(File storyDirectory) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateDirectory = sdf.format(new Date());
        //创建目录对象
        File file = new File(storyDirectory, dateDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 批量上传文件
     * @param files 文件数组
     * @return  上传完成的file对象集合
     * @throws IOException
     */
    public List<File> doUpload(MultipartFile[] files) throws IOException {
        List<File> localFiles = new ArrayList<File>();
        for (MultipartFile file : files) {
            File localFile = doUpload(file);
            localFiles.add(localFile);
        }
        return localFiles;
    }

    /**
     * 上传文件
     * @param file  文件
     * @return  完成上传的file对象
     * @throws IOException
     */
    public File doUpload(MultipartFile file) throws IOException {
        File localFile = null;
        if (file.isEmpty()) {
            //此文件域未进行上传
            System.err.println("**************************** 请选择文件！**************************");
        } else {
            //如果文件夹不存在，创建新的文件夹
            File directory = new File(uploadFolder);
            File directoryFile = makeFullDirectoryObject(directory);
            // 把文件上传至path的路径，文件名改为为当前时间+原始文件名.扩展名
            localFile = new File(directoryFile, UUID.randomUUID()+ file.getOriginalFilename());
            //执行上传
            file.transferTo(localFile);
        }
        return localFile;
    }

    /**
     * 删除文件
     * @param path 项目路径+文件路径
     */
    public void delPic(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        //判断父文件夹是否为空
        String parentPath = getParentPath(path);
        File parentFile = new File(parentPath);
        String[] list = parentFile.list();
        //为空删除父文件夹
        if(parentFile.exists()){
            if (parentFile == null || list.length == 0) {
                delFolder(parentPath);
            }
        }
    }

    /**
     * 删除文件夹
     * @param folderPath 文件夹完整绝对路径
     */
    public void delFolder(String folderPath) {
        try {
            // delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 删除指定文件夹下所有文件
     * @param path 文件夹完整绝对路径
     * @return
     */
    public boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // 先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i]);
                // 再删除空文件夹
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 得到该文件的父类文件的路径
     * @param path 绝对路径
     * @return
     */
    public String getParentPath(String path) {
        File file = new File(path);
        StringBuilder sb = new StringBuilder();
        File temp = file;
        while (temp.getParentFile() != null
                && temp.getParentFile().getName().length() != 0) {
            sb.insert(0, "/" + temp.getParentFile().getName());
            temp = temp.getParentFile();
        }
        sb.append("/");
        return sb.toString();

    }

    /**
     * 根据文件路径获取MultipartFile
     * @param picPath
     * @return
     */
    public  MultipartFile getMulFileByPath(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        if(fileItem != null){
            MultipartFile mfile = new CommonsMultipartFile(fileItem);
            return mfile;
        }else {
            return null;
        }
    }

    /**
     * 根据文件地址获取到fileItem
     * @param filePath
     * @return
     */
    public  FileItem createFileItem(String filePath) {
        System.out.println("=================="+filePath);
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
//        int num = filePath.lastIndexOf(".");
        int num = filePath.lastIndexOf("\\");
//        String extFile = filePath.substring(num);
        String extFile = filePath.substring(num+1);
        FileItem item = factory.createItem(textFieldName, "text/plain", true,
                "_"+ extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(newfile);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192))
                    != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            return null;
        }
        return item;
    }
}
