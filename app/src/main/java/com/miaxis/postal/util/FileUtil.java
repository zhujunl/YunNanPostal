package com.miaxis.postal.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tang.yf on 2018/11/15.
 */

public class FileUtil {

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "miaxis";
    public static final String MAIN_PATH = PATH + File.separator + "postal";
    public static final String LICENCE_PATH = PATH + File.separator + "FaceId_ST" + File.separator + "st_lic.txt";
    public static final String FACE_IMAGE_PATH = MAIN_PATH + File.separator + "recordImage";
    public static final String FACE_STOREHOUSE_PATH = MAIN_PATH + File.separator + "faceStorehouse";
    public static final String ORDER_STOREHOUSE_PATH = MAIN_PATH + File.separator + "orderStorehouse";
    public static final String LOCAL_STOREHOUSE_PATH = MAIN_PATH + File.separator + "localStorehouse";

    public static void initDirectory() {
        File path = new File(FileUtil.PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.MAIN_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.FACE_IMAGE_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.FACE_STOREHOUSE_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.ORDER_STOREHOUSE_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.LOCAL_STOREHOUSE_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public static String readLicence(String licencePath) {
        File lic = new File(licencePath);
        return readFileToString(lic);
    }

    public static void copyAssetsFile(Context context, String fileSrc, String fileDst) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getAssets().open(fileSrc);
            File file = new File(fileDst);
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFileToString(File file) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes 生成文件用到的byte数组
     */
    public static void createFileWithByte(byte[] bytes, String filePath) {
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File file = new File(filePath);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static String fileToBase64(File file) {
        try {
            byte[] bytes = toByteArray(file);
            String str = Base64.encodeToString(bytes, Base64.NO_WRAP);
            bytes = null;
            System.gc();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] toByteArray(File f) throws Exception {
        BufferedInputStream in = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File saveQualityBitmap(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.getFD().sync();
            out.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File saveBitmap(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            ByteArrayOutputStream baos = compressImage(bitmap);
            FileOutputStream out = new FileOutputStream(file);
//            compressImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.write(baos.toByteArray());
            out.flush();
            out.getFD().sync();
            out.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ByteArrayOutputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 80) { // 循环判断如果压缩后图片是否大于80kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
            // 这里压缩options%，把压缩后的数据存放到baos中
            if (options > 10) {//设置最小值，防止低于0时出异常
                options -= 10;// 每次都减少10
            } else {
                break;
            }
        }
        return baos;
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
//        // 把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
//        // 把ByteArrayInputStream数据生成图片
//        return bitmap;
    }

    public static Bitmap loadBitmap(String filePath) {
        try {
            return BitmapFactory.decodeFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap compressUri(Context context, Uri imageUri) {
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = context.getContentResolver().query(imageUri, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String imagePath = c.getString(columnIndex);
        c.close();

        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(imagePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
            inSampleSize = (int) ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
        return bm;
    }

    public static void deleteImg(String path) {
        try {
            File f = new File(path);
            if (!f.delete()) {
                Log.e("asd", "删除失败" + path);
            }
        } catch (Exception e) {
            Log.e("asd", "删除失败 路径：" + path + "\r\n" + e.getMessage());
        }
    }

}
