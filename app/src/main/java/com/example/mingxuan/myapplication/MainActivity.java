package com.example.mingxuan.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends Activity implements View.OnTouchListener {
    private static final int FILE_SELECT_CODE = 1001;
    private static final int FILE_SELECT_CODD = 1002;
    private String INTENT_TYPE  = "image/*";
    private Button save,xs,gz,gx;
    private final int WORDNUM = 35;  //转化成图片时  每行显示的字数
    private final int WIDTH = 450;   //设置图片的宽度
    private ImageView imageView,imageView1;
    WebView webView;
    int lastX, lastY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermisson();

        save = (Button) findViewById(R.id.save);
        xs = (Button) findViewById(R.id.xs);
        gz = (Button) findViewById(R.id.gz);
//        webView = (WebView) findViewById(R.id.webview);
        imageView = (ImageView) findViewById(R.id.image);
        imageView1 = (ImageView) findViewById(R.id.imageView);
        imageView1.setDrawingCacheEnabled(true);
        imageView1.setOnTouchListener(this);

        Bugly.init(getApplicationContext(), "75779be41b", false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fafa();
                choosefile();
            }
        });

        xs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(INTENT_TYPE);
                startActivityForResult(intent,FILE_SELECT_CODD);
            }
        });

        gz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.gs);
                Bitmap oneb = BitmapFactory.decodeFile(path);
                Bitmap bitmap = save(oneb,bm);
                imageView.setImageBitmap(bitmap);
            }
        });

        findViewById(R.id.gx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.checkUpgrade();
            }
        });



    }

    Canvas  canvas;
    public Bitmap save(Bitmap one ,Bitmap two) {
        if(one == null ){
            return null;
        }

        int w = one.getWidth();
        int h = one.getHeight();
        int tw = two.getWidth();
        int th = two.getHeight();

        //计算缩放的比例
//        float scalewidth = ((float) w) / tw;
//        float scaleheight = ((float) h) / th;

//        Matrix matrix = new Matrix();
//        matrix.postScale(scalewidth, scaleheight);
//        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Bitmap newB = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);//创建一个新的和SRC长度宽度一样的位图
        canvas = new Canvas(newB);
        canvas.drawBitmap(one,0,0,null);
        canvas.drawBitmap(two,lastX, lastY, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        String[] name = wjm.split("\\.");//文件名
        String newpath = Environment.getExternalStorageDirectory() + "/"+name[0]+"1.png";//保存路径
        Toast.makeText(MainActivity.this,"===="+path,Toast.LENGTH_LONG).show();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(newpath));
            newB.compress(Bitmap.CompressFormat.PNG, 100, os);//0意味着压缩到最小，100意味着压缩后的质量最好，PNG是无损图像，会忽略这个参数
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newB;
    }

    // 打开系统文件浏览功能
    Intent intent;
    private void choosefile(){
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);//用来指示一个GET_CONTENT意图只希望ContentResolver.openInputStream能够打开URI
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private String filepath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK &&FILE_SELECT_CODE == requestCode){
            Uri uri = data.getData();
            Log.i("uri-------->", "" + uri);
//            path = Uri.decode(data.getDataString());
//            path = uri.getPath();
            filepath = FileUtils.getPath(MainActivity.this,uri);
            System.out.println(filepath);
            Toast.makeText(MainActivity.this,""+filepath,Toast.LENGTH_LONG).show();
            wjm = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());//获取文件名
            hzm = FileUtils.getLastUrl(filepath);//获取后缀名
//            FilesActivity.this.startService(intent);
            fafa();
        }else if(resultCode == Activity.RESULT_OK &&FILE_SELECT_CODD == requestCode){
            Uri url = data.getData();
            ContentResolver resolver = getContentResolver();
            Bitmap bmp= null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(resolver,url);//获取图片
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);
        }
    }

    String wjm;//获取文件名
    String hzm;
    String path;
    private void fafa(){
        int x=5,y=10;
        try {
//            TestView tp = new TestView(WORDNUM, new InputStreamReader(getResources().getAssets().open("1.txt")));
            File file = new File(filepath);
            FileInputStream inStream = new FileInputStream(file);
            TestView tp = new TestView(WORDNUM, new InputStreamReader(inStream,"gbk"));
            Bitmap bitmap = Bitmap.createBitmap(WIDTH, 20*tp.getHeigt(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(getResources().getColor(R.color.white));
            Paint paint = new Paint();
            String [] ss = tp.getContext();
            Log.e("=====",""+ss);
            for(int i=0;i<tp.getHeigt();i++){
                canvas.drawText(ss[i], x, y, paint);
                y=y+20;
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
//            String path = Environment.getExternalStorageDirectory() + "/image.png";
            String[] name = wjm.split("\\.");//文件名
            path = Environment.getExternalStorageDirectory() + "/"+name[0]+".png";//保存路径
            Toast.makeText(MainActivity.this,"===="+path,Toast.LENGTH_LONG).show();
            FileOutputStream os = new FileOutputStream(new File(path));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);//0意味着压缩到最小，100意味着压缩后的质量最好，PNG是无损图像，会忽略这个参数
            os.flush();
            os.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    /**
     * 动态权限的请求
     */
    public void checkPermisson() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this,//上下文
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_CALENDAR
                    },//权限数组
                    1001);
        }
    }

    /**
     * 动态权限的回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                Log.e("","你点击了");
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;
                v.layout(left, top, right, bottom);

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                Log.e("","你移动了");
                break;
            case MotionEvent.ACTION_UP:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                Log.e("","你松手了"+lastX+"    "+lastY);
                break;
        }
        return true;
    }
}
