package ru.nsu.ccfit.nsumediabot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.nsumediabot.models.dto.DiskFileDTO;
import ru.nsu.ccfit.nsumediabot.models.exceptions.DiskException;
import ru.nsu.ccfit.nsumediabot.models.responses.FileInfoResponse;
import ru.nsu.ccfit.nsumediabot.models.responses.GetUploadUrlResponse;
import ru.nsu.ccfit.nsumediabot.models.responses.PublishFileResponse;
import ru.nsu.ccfit.nsumediabot.models.responses.YandexDiskErrorResponse;
import ru.nsu.ccfit.nsumediabot.service.DiskService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiskServiceImpl implements DiskService {
    private final String DISK_RESOURCE_URL = "https://cloud-api.yandex.net/v1/disk/resources/upload";
    private final String FILE_INFO_URL = "https://cloud-api.yandex.net/v1/disk/resources";
    private final String MAKE_PUBLISH_URL = "https://cloud-api.yandex.net/v1/disk/resources/publish";
    private final String DELETE_RESOURCE_URL = "https://cloud-api.yandex.net/v1/disk/resources";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${yandex.disk.api.token}")
    private String apiToken;

    @Override
    public DiskFileDTO upload(File file) {
        URL urlForUpload;
        try {
            urlForUpload = new URL(Objects.requireNonNull(getURL(file)));
        } catch (MalformedURLException exception) {
            log.error("Disk error", exception.getLocalizedMessage());
            throw new DiskException(exception.getLocalizedMessage());
        }

        Request uploadRequest = new Request.Builder()
                .url(urlForUpload)
                .put(RequestBody.create(null, file))
                .build();

        try {
            Response uploadResponse = client.newCall(uploadRequest).execute();
            if (!uploadResponse.isSuccessful()) {
                handleError(uploadResponse);
            }

            return getFileInfo(file);
        } catch (IOException exception) {
            log.error(exception.getLocalizedMessage());
            throw new DiskException(exception.getLocalizedMessage());
        }
    }

    private String getURL(File file) {
        Request request = new Request.Builder()
                .url(DISK_RESOURCE_URL + "?path=" + file.getName())
                .addHeader("Authorization", apiToken)
                .get()
                .build();

        GetUploadUrlResponse getUploadUrlResponse = new GetUploadUrlResponse();
        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                getUploadUrlResponse = objectMapper.readValue(response.body().string(), GetUploadUrlResponse.class);
            } else {
                handleError(response);
            }
        } catch (IOException exception) {
            log.error(exception.getLocalizedMessage());
        }

        return getUploadUrlResponse.getHref();
    }

    private DiskFileDTO getFileInfo(File file) {
        makeFilePublish(file);

        Request infoRequest = new Request.Builder()
                .url(FILE_INFO_URL + "?path=" + file.getName())
                .addHeader("Authorization", apiToken)
                .get()
                .build();

        FileInfoResponse fileInfoResponse = new FileInfoResponse();
        try {
            Response infoResponse = client.newCall(infoRequest).execute();
            if (infoResponse.isSuccessful()) {
                fileInfoResponse = objectMapper.readValue(infoResponse.body().string(), FileInfoResponse.class);
            } else {
                handleError(infoResponse);
            }
        } catch (IOException exception) {
            log.error("Disk error", exception.getLocalizedMessage());
        }

        DiskFileDTO dto = new DiskFileDTO();
        dto.setFileLink(fileInfoResponse.getFile());
        dto.setDiskFilePath(fileInfoResponse.getPath());

        return dto;
    }

    private void makeFilePublish(File file) {

        Request makePublicRequest = new Request.Builder()
                .url(MAKE_PUBLISH_URL + "?path=" + "disk%3A%2F" + file.getName())
                .addHeader("Authorization", apiToken)
                .put(RequestBody.create(null, new byte[0]))
                .build();

        Response makePublicResponse;
        try {
            makePublicResponse = client.newCall(makePublicRequest).execute();

            if (!makePublicResponse.isSuccessful()) {
                handleError(makePublicResponse);
            }
        } catch (IOException exception) {
            log.error(exception.getLocalizedMessage());
            throw new DiskException(exception.getLocalizedMessage());
        }
    }

    private void handleError(Response response) {
        YandexDiskErrorResponse yandexDiskErrorResponse;
        try {
            yandexDiskErrorResponse = objectMapper.readValue(response.body().string(), YandexDiskErrorResponse.class);
        } catch (IOException exception) {
            throw new DiskException("Failed to parse error response");
        }

        throw new DiskException(yandexDiskErrorResponse.getMessage());
    }

    @Override
    public void delete(String diskFilePath) {
        Request request = new Request.Builder()
                .url(DELETE_RESOURCE_URL + "?path=" + diskFilePath + "&permanently=true")
                .addHeader("Authorization", apiToken)
                .delete()
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException exception) {
            log.error(exception.getLocalizedMessage());
            throw new DiskException(exception.getLocalizedMessage());
        }
    }
}
