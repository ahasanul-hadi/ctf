package com.cirt.ctf.submission;

import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.marking.ResultDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions")
public class SubmissionController {
    private final ModelMapper modelMapper;
    private final SubmissionService submissionService;
    private final DocumentService documentService;

    @GetMapping
    public String getSubmissions(Model model){
        model.addAttribute("submissions",submissionService.findAll());
        return "submission/submission";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/mark/{id}")
    public String getSubmission(@PathVariable("id") Long id,  Model model){

        SubmissionDTO submissionDTO= submissionService.findById(id);

        model.addAttribute("resultDTO", new ResultDTO());
        model.addAttribute("submission",submissionDTO);
        model.addAttribute("document",documentService.findById(submissionDTO.getDocumentID()));
        return "marking/mark";
    }
}
