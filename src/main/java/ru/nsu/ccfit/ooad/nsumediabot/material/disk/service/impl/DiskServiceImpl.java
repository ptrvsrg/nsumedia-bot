package ru.nsu.ccfit.ooad.nsumediabot.material.disk.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.ooad.nsumediabot.material.disk.exception.DiskException;
import ru.nsu.ccfit.ooad.nsumediabot.material.disk.response.DiskErrorResponse;
import ru.nsu.ccfit.ooad.nsumediabot.material.disk.response.FileInfoResponse;
import ru.nsu.ccfit.ooad.nsumediabot.material.disk.response.GetUploadUrlResponse;
import ru.nsu.ccfit.ooad.nsumediabot.material.disk.service.DiskService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiskServiceImpl
        implements DiskService {

    private static final String FILE_ALREADY_EXISTS_RESPONSE = "DiskPathPointsToExistentDirectoryError";
    private static final String FILE_NOT_FOUND_RESPONSE = "DiskNotFoundError";
    private static final String CREATE_DIRECTORY_URL = "https://cloud-api.yandex.net/v1/disk/resources";
    private static final String GET_UPLOAD_URL_URL = "https://cloud-api.yandex.net/v1/disk/resources/upload";
    private static final String PUBLISH_FILE_URL = "https://cloud-api.yandex.net/v1/disk/resources/publish";
    private static final String GET_FILE_INFO_URL = "https://cloud-api.yandex.net/v1/disk/resources";
    private static final String DELETE_FILE_URL = "https://cloud-api.yandex.net/v1/disk/resources";

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @Value("${yandex.disk.api.token}")
    private String apiToken;

    @Override
    public String uploadFile(String path, File file) {
        createPath(path);

        String uploadUrl = getUploadUrl(path);

        Request request = new Request.Builder()
                .url(uploadUrl)
                .put(RequestBody.create(null, file))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new DiskException(mapToErrorResponse(response).getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        publishFile(path);

        String publicUrl = getPublicUrl(path);
        log.info("File {} uploaded to disk", path);
        return publicUrl;
    }

    @Override
    public void deleteFile(String path) {
        URL url = HttpUrl.parse(DELETE_FILE_URL)
                .newBuilder()
                .addQueryParameter("path", path)
                .build()
                .url();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "OAuth " + apiToken)
                .delete()
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new DiskException(mapToErrorResponse(response).getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("File {} deleted from disk", path);
    }

    private DiskErrorResponse mapToErrorResponse(Response response) {
        try {
            return objectMapper.readValue(
                    response.body().string(),
                    DiskErrorResponse.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T mapToSuccessResponse(Response response, Class<T> clazz) {
        try {
            return objectMapper.readValue(response.body().string(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createPath(String path) {
        String[] dirs = path.split("/");
        if (dirs[0].isEmpty()) {
            dirs = Arrays.copyOfRange(dirs, 1, dirs.length - 1);
        } else {
            dirs = Arrays.copyOfRange(dirs, 0, dirs.length - 1);
        }

        StringBuilder createdPath = new StringBuilder();
        for (String dir : dirs) {
            createdPath.append("/").append(dir);
            URL url = HttpUrl.parse(CREATE_DIRECTORY_URL)
                    .newBuilder()
                    .addQueryParameter("path", createdPath.toString())
                    .build()
                    .url();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "OAuth " + apiToken)
                    .put(RequestBody.create(null, ""))
                    .build();

            try {
                Response response = okHttpClient.newCall(request)
                        .execute();
                if (!response.isSuccessful()) {
                    DiskErrorResponse diskErrorResponse = mapToErrorResponse(response);
                    if (!Objects.equals(diskErrorResponse.getError(), FILE_ALREADY_EXISTS_RESPONSE)) {
                        throw new DiskException(diskErrorResponse.getMessage());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getUploadUrl(String path) {
        URL url = HttpUrl.parse(GET_UPLOAD_URL_URL)
                .newBuilder()
                .addQueryParameter("path", path)
                .build()
                .url();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "OAuth " + apiToken)
                .get()
                .build();

        try {
            Response response = okHttpClient.newCall(request)
                    .execute();
            if (response.isSuccessful()) {
                return mapToSuccessResponse(response, GetUploadUrlResponse.class).getHref();
            }
            throw new DiskException(mapToErrorResponse(response).getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void publishFile(String path) {
        URL url = HttpUrl.parse(PUBLISH_FILE_URL)
                .newBuilder()
                .addQueryParameter("path", path)
                .build()
                .url();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "OAuth " + apiToken)
                .put(RequestBody.create(null, ""))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new DiskException(mapToErrorResponse(response).getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPublicUrl(String path) {
        URL url = HttpUrl.parse(GET_FILE_INFO_URL)
                .newBuilder()
                .addQueryParameter("path", path)
                .build()
                .url();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "OAuth " + apiToken)
                .get()
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return mapToSuccessResponse(response, FileInfoResponse.class).getPublicUrl();
            }
            throw new DiskException(mapToErrorResponse(response).getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
