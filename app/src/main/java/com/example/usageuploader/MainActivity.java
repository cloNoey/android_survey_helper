package com.example.usageuploader;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final String SERVER_URL = BuildConfig.API_URL; // 실제 서버 주소로 변경

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUpload = findViewById(R.id.btn_upload);

        btnUpload.setOnClickListener(v -> {
            if (!hasUsageStatsPermission()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(this, "사용 기록 접근 권한을 허용해 주세요!", Toast.LENGTH_LONG).show();
            } else {
                try {
                    File csvFile = CsvUtils.generateUsageCsv(this);
                    ApiClient.uploadCsv(csvFile, SERVER_URL, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("UPLOAD", "onFailure: ", e);
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resp = response.body() != null ? response.body().string() : "No body";
                            Log.d("UPLOAD", "onResponse: " + response.code() + ", " + resp);
                            runOnUiThread(() -> {
                                if (response.isSuccessful())
                                    Toast.makeText(MainActivity.this, "업로드 성공!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MainActivity.this, "업로드 실패: " + response.code() + "\n" + resp, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "CSV 생성 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 권한 확인 함수
    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}