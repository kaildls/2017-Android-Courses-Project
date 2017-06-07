package com.example.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QRcode_Make extends AppCompatActivity {

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRcode_Make.this);
        builder.setMessage("无法生成内容为空的二维码！");
        builder.setTitle("警告");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void dialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRcode_Make.this);
        builder.setMessage("请输入需要生成的二维码的大小！");
        builder.setTitle("警告");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints= new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j,i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_make);
        Button button1 = (Button)findViewById(R.id.b2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button button = (Button)findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText1 = (EditText)findViewById(R.id.et1);
                EditText editText2 = (EditText)findViewById(R.id.et2);
                String string = editText1.getText().toString();
                String string1 = editText2.getText().toString();
                if (string1.isEmpty()) editText2.setText("960");
                int width = Integer.parseInt(editText2.getText().toString());
                if (string.isEmpty()) {
                    dialog();
                } else {
                    Bitmap qrBitmap = generateBitmap(string, width, width);
                    ImageView imageView = (ImageView)findViewById(R.id.iv1);
                    imageView.setImageBitmap(qrBitmap);
                    TextView textView3 = (TextView)findViewById(R.id.tv3);
                    textView3.setText("长按保存图片");
                }
            }
        });
        ImageView imageView = (ImageView)findViewById(R.id.iv1);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog1();
                return false;
            }
        });
    }

    protected void dialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRcode_Make.this);
        builder.setMessage("是否将图片保存到本地");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ImageView imageView = (ImageView)findViewById(R.id.iv1);
                if (ContextCompat.checkSelfPermission(QRcode_Make.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRcode_Make.this, new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    saveImageView(imageView);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    /*
    private void saveImageView(ImageView imageView){
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
        imageView.setDrawingCacheEnabled(false);
    }
    */
    public class SaveImageUtils extends AsyncTask<Bitmap, Void, String> {
        Activity mActivity;
        ImageView mImageView;
        public SaveImageUtils(Activity activity, ImageView imageView) {
            this.mImageView = imageView;
            this.mActivity = activity;
        }
        @Override
        protected String doInBackground(Bitmap... params) {
            String result = mActivity.getResources().getString(R.string.save_picture_failed);
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/QRCode");
                if (!file.exists()) {
                    file.mkdirs();
                }
                File imageFile = new File(file.getAbsolutePath(), System.currentTimeMillis() + ".jpg");
                FileOutputStream outStream = null;
                outStream = new FileOutputStream(imageFile);
                Bitmap image = params[0];
                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                result = mActivity.getResources().getString(R.string.save_picture_success, file.getAbsolutePath());
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(imageFile)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mActivity, result, Toast.LENGTH_SHORT).show();
            mImageView.setDrawingCacheEnabled(false);
        }
    }

    public void saveImageView(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        if (bitmap != null) {
            new SaveImageUtils(QRcode_Make.this, imageView).execute(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ImageView imageView = (ImageView)findViewById(R.id.iv1);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImageView(imageView);
                } else {
                    Toast.makeText(this, "您拒绝了访问存储", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}
