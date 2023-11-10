package ru.nsu.ccfit.nsumediabot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiskServiceImpl implements DiskService {
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private ObjectMapper mapper;

    @Value("${yandex.disk.api.token}")
    private String apiToken;

    @Override
    public DiskFileDTO upload(File file) {
        try {
            URL urlForUpload = new URL(Objects.requireNonNull(getURL(file)));
            Request uploadRequest = new Request.Builder()
                    .url(urlForUpload)
                    .put(RequestBody.create(null, file))
                    .build();
            Response uploadResponse = client.newCall(uploadRequest).execute();

            if (uploadResponse.isSuccessful()) {
                return makeFilePublish(file);
            } else {
                handleError(uploadResponse);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            log.error(exception.getLocalizedMessage());
            throw new DiskException(exception.getMessage());
        }

        return null;
    }

    private String getURL(File file) {
        final String diskResource = "https://cloud-api.yandex.net/v1/disk/resources/upload";
        Request request = new Request.Builder()
                .url(diskResource + "?path=" + file.getName())
                .addHeader("Authorization", apiToken)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                GetUploadUrlResponse getUploadUrlResponse = mapper.readValue(response.body().string(), GetUploadUrlResponse.class);
                return getUploadUrlResponse.getHref();
            } else {
                handleError(response);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            log.error(exception.getLocalizedMessage());
        }

        return null;
    }

    private DiskFileDTO makeFilePublish(File file) {
        final String makePublicURL = "https://cloud-api.yandex.net/v1/disk/resources/publish";

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
            } else {
                handleError(makePublicResponse);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            log.error(exception.getLocalizedMessage());
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
        final String deleteResource = "https://cloud-api.yandex.net/v1/disk/resources";

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
