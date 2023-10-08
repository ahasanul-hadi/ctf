package com.cirt.ctf.document;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequiredArgsConstructor
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;

    @RequestMapping(value = { "/api/document/preview/{id}" }, method = RequestMethod.GET)
    public void getDocumentById(HttpServletResponse response, HttpServletRequest request, @PathVariable int id)
            throws Exception {
        DocumentEntity dto = documentService.findById(id).orElseThrow(()->new Exception("No document Found!"));

        // logger.error("UPLOADED_FOLDER: " + GlobalConstants.UPLOADED_FOLDER);

        File file = new File(dto.getFileLocation());
        FileInputStream inStream = new FileInputStream(file);

        // gets MIME type of the file
        String mimeType = "application/octet-stream";
        OutputStream outStream = response.getOutputStream();


        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) file.length());

        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", dto.getOriginalFileName());
        response.setHeader(headerKey, headerValue);

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();

    }

}
