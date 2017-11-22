package io.weicools.puremusic.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.weicools.puremusic.AppCache;
import io.weicools.puremusic.R;
import io.weicools.puremusic.http.HttpCallback;
import io.weicools.puremusic.http.HttpClient;
import io.weicools.puremusic.model.Splash;
import io.weicools.puremusic.service.EventCallback;
import io.weicools.puremusic.service.MusicService;
import io.weicools.puremusic.ui.base.BaseActivity;
import io.weicools.puremusic.util.FileUtil;
import io.weicools.puremusic.util.PermissionUtil;
import io.weicools.puremusic.util.Preferences;
import io.weicools.puremusic.util.ToastUtil;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    private static final String SPLASH_FILE_NAME = "Splash";

    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    @BindView(R.id.tv_copyright)
    TextView mTvCopyright;

    private Context mContext;
    private ServiceConnection mMusicServiceConnection;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mContext = this;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        mTvCopyright.setText(getString(R.string.copyright, year));

        checkService();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        if (mMusicServiceConnection != null) {
            unbindService(mMusicServiceConnection);
        }
        super.onDestroy();
    }

    private void checkService() {
        if (AppCache.getPlayService() == null) {
            startService();
            showSplash();
            updateSplash();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindService();
                }
            }, 1000);
        } else {
            startMusicActivity();
            finish();
        }
    }

    private void startService() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        mMusicServiceConnection = new MusicServiceConnection();
        bindService(intent, mMusicServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void scanMusic(final MusicService musicService) {
        musicService.updateMusicList(new EventCallback<Void>() {
            @Override
            public void onEvent(Void aVoid) {
                startMusicActivity();
                finish();
            }
        });
    }

    private void showSplash() {
        File splashImg = new File(FileUtil.getSplashDir(this), SPLASH_FILE_NAME);
        if (splashImg.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(splashImg.getPath());
            mIvSplash.setImageBitmap(bitmap);
        }
    }

    private void updateSplash() {
        HttpClient.getSplash(new HttpCallback<Splash>() {
            @Override
            public void onSuccess(Splash response) {
                if (response == null || TextUtils.isEmpty(response.getUrl())) {
                    return;
                }

                final String url = response.getUrl();
                String lastImgUrl = Preferences.getSplashUrl();
                if (TextUtils.equals(lastImgUrl, url)) {
                    return;
                }

                HttpClient.downloadFile(url, FileUtil.getSplashDir(AppCache.getContext()), SPLASH_FILE_NAME,
                        new HttpCallback<File>() {
                            @Override
                            public void onSuccess(File file) {
                                Preferences.saveSplashUrl(url);
                            }

                            @Override
                            public void onFail(Exception e) {
                            }
                        });
            }

            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "updateSplash error: " + e.getMessage());
            }
        });
    }

    private void startMusicActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtras(getIntent());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private class MusicServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            final MusicService playService = ((MusicService.MusicBinder) iBinder).getService();
            AppCache.setPlayService(playService);
            PermissionUtil.with(SplashActivity.this)
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .result(new PermissionUtil.Result() {
                        @Override
                        public void onGranted() {
                            scanMusic(playService);
                        }

                        @Override
                        public void onDenied() {
                            ToastUtil.showShort(mContext, mContext.getString(R.string.app_name));
                            finish();
                            playService.quit();
                        }
                    })
                    .request();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
