package com.example.service.impl;

import com.example.model.exceptions.DiskException;
import com.example.model.responses.GetUrlSuccessResponse;
import com.example.model.responses.HttpErrorResponse;
import com.example.model.responses.MakePublicAccessResponse;
import com.example.service.DiskService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
    public String upload(File file) {
        try {
            mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
            String diskResource = "https://cloud-api.yandex.net/v1/disk/resources/upload";
            Request request = new Request.Builder()
                    .url(diskResource + "?path=" + file.getName())
                    .addHeader("Authorization", apiToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                GetUrlSuccessResponse getUrlSuccessResponse = mapper.readValue(response.body().string(), GetUrlSuccessResponse.class);
                URL url = new URL(getUrlSuccessResponse.getHref());
                Request uploadRequest = new Request.Builder()
                        .url(url)
                        .put(RequestBody.create(null, file))
                        .build();
                Response uploadResponse = client.newCall(uploadRequest).execute();

                if (uploadResponse.isSuccessful()) {
                    return makeFilePublic(file);
                } else {
                    try {
                        getApiError(uploadResponse);
                    } catch (RuntimeException exception) {
                        exception.printStackTrace();
                        return exception.getMessage();
                    }
                }

            } else {
                try {
                    getApiError(response);
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                    return exception.getMessage();
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }

        return null;
    }

    private String makeFilePublic(File file) throws IOException {
        String makePublicURL = "https://cloud-api.yandex.net/v1/disk/resources/publish";

        Request makePublicRequest = new Request.Builder()
                .url(makePublicURL + "?path=" + "disk%3A%2F" + file.getName())
                .addHeader("Authorization", apiToken)
                .put(RequestBody.create(null, new byte[0]))
                .build();
        Response makePublicResponse = client.newCall(makePublicRequest).execute();
        System.out.println("Make public response: " + makePublicResponse.body().string());

        if (makePublicResponse.isSuccessful()) {
            try {
                MakePublicAccessResponse makePublicAccessResponse = mapper.readValue(makePublicResponse.body().string(), MakePublicAccessResponse.class);
                return makePublicAccessResponse.getHref();
            } catch (MismatchedInputException exception) {
                exception.printStackTrace();
                return "Error: Invalid JSON response";
            }
        } else {
            try {
                getApiError(makePublicResponse);
            } catch (RuntimeException exception) {
                exception.printStackTrace();
                return exception.getMessage();
            }
        }

        return null;
    }

    private void getApiError(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpErrorResponse httpErrorResponse = mapper.readValue(response.body().string(), HttpErrorResponse.class);
        throw new DiskException(httpErrorResponse.getMessage());
    }

    @Override
    public String delete(String link) {
        try {
            String deleteResource = "https://cloud-api.yandex.net/v1/disk/resources";

            Request request = new Request.Builder()
                    .url(deleteResource + "?path=" + link + "&permanently=true")
                    .addHeader("Authorization", apiToken)
                    .delete()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }

}
