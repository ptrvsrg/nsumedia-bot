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
import ru.nsu.ccfit.nsumediabot.models.responses.GetUploadUrlResponse;
import ru.nsu.ccfit.nsumediabot.models.responses.PublishFileResponse;
import ru.nsu.ccfit.nsumediabot.models.responses.YandexDiskErrorResponse;
import ru.nsu.ccfit.nsumediabot.service.DiskService;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiskServiceImpl implements DiskService {
    private final OkHttpClient client = new OkHttpClient();

    private ObjectMapper mapper;

    @Value("${yandex.disk.api.token}")
    private String apiToken;

    @Override
    public DiskFileDTO upload(File file) {
        try {
            String diskResource = "https://cloud-api.yandex.net/v1/disk/resources/upload";
            Request request = new Request.Builder()
                    .url(diskResource + "?path=" + file.getName())
                    .addHeader("Authorization", apiToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                GetUploadUrlResponse getUrlSuccessResponse = mapper.readValue(response.body().string(), GetUploadUrlResponse.class);
                URL url = new URL(getUrlSuccessResponse.getHref());
                Request uploadRequest = new Request.Builder()
                        .url(url)
                        .put(RequestBody.create(null, file))
                        .build();
                Response uploadResponse = client.newCall(uploadRequest).execute();

                if (uploadResponse.isSuccessful()) {
                    return makeFilePublish(file);
                } else {
                    try {
                        handleError(uploadResponse);
                    } catch (RuntimeException exception) {
                        exception.printStackTrace();
                        log.error(exception.getLocalizedMessage());
                    }
                }

            } else {
                try {
                    handleError(response);
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                    log.error(exception.getLocalizedMessage());
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getLocalizedMessage());
        }

        return null;
    }

    private DiskFileDTO makeFilePublish(File file) {
        String makePublicURL = "https://cloud-api.yandex.net/v1/disk/resources/publish";

        Request makePublicRequest = new Request.Builder()
                .url(makePublicURL + "?path=" + "disk%3A%2F" + file.getName())
                .addHeader("Authorization", apiToken)
                .put(RequestBody.create(null, new byte[0]))
                .build();

        Response makePublicResponse;
        PublishFileResponse publishFileResponse = new PublishFileResponse();
        DiskFileDTO diskFileDTO = new DiskFileDTO();

        try {
            makePublicResponse = client.newCall(makePublicRequest).execute();
            if (makePublicResponse.isSuccessful()) {
                publishFileResponse = mapper.readValue(makePublicResponse.body().string(), PublishFileResponse.class);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new DiskException(exception.getMessage());
        }

        diskFileDTO.setFileLink(publishFileResponse.getHref());
        return diskFileDTO;
    }

    private void handleError(Response response) {
        YandexDiskErrorResponse yandexDiskErrorResponse = new YandexDiskErrorResponse();
        try {
            yandexDiskErrorResponse = mapper.readValue(response.body().string(), YandexDiskErrorResponse.class);
        } catch (IOException exception) {
            throw new DiskException(yandexDiskErrorResponse.getMessage());
        }
    }

    @Override
    public void delete(String diskFilePath) {
        String deleteResource = "https://cloud-api.yandex.net/v1/disk/resources";

        Request request = new Request.Builder()
                .url(deleteResource + "?path=" + diskFilePath + "&permanently=true")
                .addHeader("Authorization", apiToken)
                .delete()
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
            log.error(exception.getLocalizedMessage());
            throw new DiskException(exception.getMessage());
        }
    }
}
