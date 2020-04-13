package com.ramon.ramonrm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.webapi.ReqData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class ImageUitl {
    public static File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/image");//设置保存路径

    public static Bitmap getImage(Context context, String fileName, String imageUrl, int defImageRes) {
        if (fileName == null || fileName.length() == 0 || fileName.toLowerCase().equals("null")) {
            return BitmapFactory.decodeResource(context.getResources(), defImageRes);
        }
        Bitmap img = getLocalImageDefPath(fileName);
        if (img == null) {
            img = getUrlImage(imageUrl);
            if (img != null) {
                saveImageDefPath(img, fileName);
            }
        }
        if (img != null) {
            return img;
        } else {
            return BitmapFactory.decodeResource(context.getResources(), defImageRes);
        }
    }

    public static Bitmap getUrlImage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);//读取图像数据
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static Bitmap getLocalImageDefPath(String fileName) {
        Bitmap bitmap = null;
        try {
            File avaterFile = new File(PHOTO_DIR, fileName);
            if (avaterFile.exists()) {
                bitmap = BitmapFactory.decodeFile(PHOTO_DIR + "/" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void saveImageDefPath(Bitmap bitMap, String fileName) {
        File avaterFile = new File(PHOTO_DIR, fileName);//设置文件名称
        if (avaterFile.exists()) {
            return;
        }
        try {
            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            bitMap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveImage(Bitmap bitMap, String fileName) {
        File avaterFile = new File(fileName);//设置文件名称
        if (avaterFile.exists()) {
            avaterFile.delete();
        }
        try {
            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            bitMap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getLocalImage(String fileName) {
        Bitmap bitmap = null;
        try {
            int angle = readPictureDegree(fileName);
            File avaterFile = new File(fileName);
            if (avaterFile.exists()) {
                bitmap = BitmapFactory.decodeFile(fileName);
                if (angle != 0) {
                    bitmap = rotaingImageView(angle, bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap imageYaSuo(Bitmap bitmap) {
        int toWidth = 0;
        int toHeight = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width / 1000;
        if (size > 1) {
            toWidth = width / size;
            toHeight = height / size;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            if (os.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                os.reset();// 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);// 这里压缩50%，把压缩后的数据存放到baos中
            }
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap image = BitmapFactory.decodeStream(is, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = toHeight;
            float ww = toWidth;

            newOpts.inSampleSize = (int) size;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            is = new ByteArrayInputStream(os.toByteArray());
            image = BitmapFactory.decodeStream(is, null, newOpts);
            return image;
        } else {
            return bitmap;
        }
    }

    public static Bitmap drawTextToLeftTop(Bitmap bitmap, String text, int color) {
        int width = bitmap.getWidth(), hight = bitmap.getHeight();
        Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
        Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些

        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());//创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
        canvas.drawBitmap(bitmap, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
        textPaint.setTextSize(30f);//字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
        textPaint.setColor(color);//采用的颜色
        //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
        canvas.drawText(text, 10, 50, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制
        canvas.save();
        canvas.restore();
        return icon;
    }

    public static String uploadImage(String url, String fileName, HashMap<String, String> params, final Handler handler) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(fileName);
        // MediaType.parse() 里面是上传的文件类型。
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        // 参数分别为， 请求key ，文件名称 ， RequestBody
        requestBody.addFormDataPart("file", fileName, body);
        for (String key : params.keySet()) {
            requestBody.addFormDataPart(key, params.get(key));
        }

        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = 0;
                message.obj = string;
                if (handler != null)
                    handler.sendMessage(message);
            }
        });
        return "";
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static  InputStream getUrlFile(String url){
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}