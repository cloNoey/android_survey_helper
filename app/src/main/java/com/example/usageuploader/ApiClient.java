package com.example.usageuploader;

import java.io.File;
import okhttp3.*;

public class ApiClient {
    public static void uploadCsv(File file, String serverUrl, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType CSV = MediaType.parse("text/csv");

        RequestBody fileBody = RequestBody.create(file, CSV);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}