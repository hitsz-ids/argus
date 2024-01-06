package io.ids.argus.entry.pojo.condition;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadCondition {
    @NotBlank
    private String module;
    @NotBlank
    private String fileName;
    @NonNull
    private MultipartFile file;
    private String directory;
}
