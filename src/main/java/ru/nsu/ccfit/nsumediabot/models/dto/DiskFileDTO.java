package ru.nsu.ccfit.nsumediabot.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DiskFileDTO {
    private String diskFilePath;
    private String fileLink;
}
