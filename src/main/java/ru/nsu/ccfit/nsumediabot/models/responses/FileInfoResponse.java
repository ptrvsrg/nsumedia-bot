package ru.nsu.ccfit.nsumediabot.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoResponse {
    private String public_key;
    private Embedded _embedded;
    private String name;
    private String created;
    private Map<String, String> custom_properties;
    private String public_url;
    private String modified;
    private String path;
    private String type;
    private String antivirus_status;
    private Map<String, String> exif;
    private Integer size;
    private String resource_id;
    private Map<String, String> comment_ids;
    private String private_source;
    private String pub;
    private String mime_type;
    private String file;
    private String media_type;
    private String sha256;
    private String md5;
    private String revision;
    private ArrayList<Preview> sizes;
    private String preview;

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
        private String mime_type;
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
