package com.cirt.ctf.document;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${document.upload.directory}")
    private String UPLOAD_DIR;

    private Path root=null;

    @PostConstruct
    public void init() {
        try {
            root = Paths.get(UPLOAD_DIR);
            Files.createDirectories(root);
        } catch (IOException e) {
            //throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public Optional<DocumentEntity> findById(Long id){
        if(id==null)
            return Optional.empty();
        return documentRepository.findById(id);
    }

    public DocumentEntity saveDocument(MultipartFile file){
        try {
            String extension="";
            int index = file.getOriginalFilename().lastIndexOf('.');
            if (index > 0) {
                extension = file.getOriginalFilename().substring(index+1);
            }
            String saveFileName= System.currentTimeMillis()+"."+extension;

            String absPath= this.root.resolve(saveFileName).toAbsolutePath().toString();
            log.info("absPath:"+absPath);
            Files.copy(file.getInputStream(), Path.of(absPath), StandardCopyOption.REPLACE_EXISTING);

            DocumentEntity doc= DocumentEntity.builder()
                    .saveFileName(saveFileName)
                    .originalFileName(file.getOriginalFilename())
                    .fileLocation(absPath).build();


            doc= documentRepository.save(doc);
            return doc;
        } catch (Exception e) {
            log.error("file upload error:"+e.toString());
        }
        return null;
    }
}
