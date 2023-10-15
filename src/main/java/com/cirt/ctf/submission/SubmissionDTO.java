package com.cirt.ctf.submission;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Getter
public class SubmissionDTO {
    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionTime;
    private String filePath;
    private String documentID;
    private ChallengeEntity challenge;
    private User solver;
    private TeamEntity team;
    private boolean isVerified;
    private ResultEntity result;
    private MultipartFile file;
    private long challengeID;

    public String getVerifiedString(){
        return isVerified?"Yes":"No";
    }


}
