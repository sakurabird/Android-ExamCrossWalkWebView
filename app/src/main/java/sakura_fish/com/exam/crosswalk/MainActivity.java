package sakura_fish.com.exam.crosswalk;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.ValueCallback;
import android.widget.ImageView;

import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private XWalkView mXWalkView;
    private ImageView mImageView;
    private ValueCallback<Uri> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mXWalkView = (XWalkView) findViewById(R.id.xwalkview);
        mImageView = (ImageView) findViewById(R.id.imageview);
        settingXWalkView();
        mXWalkView.load("file:///android_asset/index.html", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXWalkView != null) {
            mXWalkView.pauseTimers();
            mXWalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXWalkView != null) {
            mXWalkView.resumeTimers();
            mXWalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXWalkView != null) {
            mXWalkView.onDestroy();
        }
    }

    protected void settingXWalkView() {
        mXWalkView.setUIClient(new MyUIClient(mXWalkView));
    }

    class MyUIClient extends XWalkUIClient {
        MyUIClient(XWalkView view) {
            super(view);
        }

        @Override
        public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            Log.d(MainActivity.class.getSimpleName(), "openFileChooser");
            super.openFileChooser(view, uploadFile, acceptType, capture);
            mUploadMessage = uploadFile;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(MainActivity.class.getSimpleName(), "requestCode:" + requestCode + " resultCode:" + resultCode);

        if (mUploadMessage == null) return;
        Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
        if (result == null) return;
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;

        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        try {
            bm = MediaStore.Images.Media.getBitmap(resolver, result);
            mImageView.setImageBitmap(bm);
            bm = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
