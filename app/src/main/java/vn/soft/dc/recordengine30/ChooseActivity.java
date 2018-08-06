package vn.soft.dc.recordengine30;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;
    @BindView(R.id.btnSing)
    TextView btnSing;
    @BindView(R.id.btnSetup)
    TextView btnSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ButterKnife.bind(this);

        claimPermission();
    }

    private void claimPermission() {
        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }

        requestPermission(PERMISSION_CODE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public boolean isPermissionGranted(String... permissions) {
        for (String value : permissions) {
            if (isPermissionGranted(value)) continue;
            return false;
        }
        return true;
    }

    public boolean isPermissionGranted(String permissions) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        claimPermission();
    }

    public void requestPermission(int requestCode, String... permissions) {
        requestPermission(permissions, requestCode);
    }

    public void requestPermission(String[] permissionList, int codeRequest) {
        ActivityCompat.requestPermissions(this, permissionList, codeRequest);
    }

    @OnClick({R.id.btnSing, R.id.btnSetup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSing:
                MainActivity.start(ChooseActivity.this);
                break;
            case R.id.btnSetup:
                DevActivity.start(ChooseActivity.this);
                break;
        }
    }
}
