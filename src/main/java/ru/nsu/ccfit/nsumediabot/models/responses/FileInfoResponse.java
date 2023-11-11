package ru.nsu.ccfit.nsumediabot.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoResponse {

    @JsonProperty("antivirus_status")
    private String antivirusStatus;
    @JsonProperty("public_key")
    private String publicKey;
    @JsonProperty("public_url")
    private String publicUrl;
    private String name;
    private Map<String, String> exif;
    private Date created;
    private Integer size;
    @JsonProperty("resource_id")
    private String resourceId;
    private Date modified;
    @JsonProperty("mime_type")
    private String mimeType;
    @JsonProperty("comment_ids")
    private Map<String, String> commentIds;
    private List<Preview> sizes;
    private String file;
    @JsonProperty("media_type")
    private String mediaType;
    private String preview;
    private String path;
    private String sha256;
    private String type;
    private String md5;
    private String revision;
    @JsonProperty("_embedded")
    private Embedded embedded;
    @JsonProperty("custom_properties")
    private Map<String, String> customProperties;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Embedded {
        private String sort;
        private String path;
        private List<Item> items;
        private int limit;
        private int offset;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String path;
        private String type;
        private String name;
        private String modified;
        private String created;
        private String preview;
        private String md5;
        @JsonProperty("mime_type")
        private String mimeType;
        private Integer size;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Preview {
        private String url;
        private String name;
    }
}