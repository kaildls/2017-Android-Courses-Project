package com.example.tools;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;

public class QRcode_Scan extends AppCompatActivity {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_scan);
        Button button = (Button)findViewById(R.id.Bn2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTextView = (TextView)findViewById(R.id.result);
        mImageView = (ImageView)findViewById(R.id.qrcode_bitmap);
        Button mButton = (Button)findViewById(R.id.Bn1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QRcode_Scan.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView textView = (TextView)v;
                ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(textView.getText().toString().trim());
                Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mTextView.setText(bundle.getString("result"));
                    mImageView.setImageBitmap((Bitmap)data.getParcelableExtra("bitmap"));
                    TextView textView = (TextView)findViewById(R.id.tips);
                    textView.setText("（长按上面文字可复制）");
                }
                break;
        }
    }
}
