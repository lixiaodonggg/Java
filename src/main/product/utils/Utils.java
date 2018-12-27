package main.product.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public final class Utils {
    public static void downLoad(OutputStream out, String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath); //地址都是绝对的
        int len;
        byte[] bytes = new byte[1024];
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
        }
        in.close();
        out.close();
    }

    public static String getMD5String(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String removeSuffix(String fileName) {   //获文件名
        if (!fileName.contains(".")) {
            return fileName;
        } else if (fileName.contains(File.separator)) {
            fileName = getFilename(fileName);
        } else {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String getFilename(String path) {
        path = path.replace(File.separator, "/");
        int index = path.lastIndexOf('/');
        String str = path.substring(index + 1);
        String fileName = str.substring(0, str.lastIndexOf("."));
        return setRightAttr(fileName);
    }

    public static String setRightAttr(String attr) {
        attr = attr.replaceAll("\\s*", "");
        return attr;
    }
}
