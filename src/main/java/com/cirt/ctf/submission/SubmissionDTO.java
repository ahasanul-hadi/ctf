package com.cirt.ctf.submission;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDTO {
    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionTime;
    private String filePath;
    private String documentID;
    private ChallengeEntity challenge;
    private User solver;
    private TeamEntity team;
    private User takenBy;
    private boolean isPublished=false;
    private Integer mark;
    private Integer penalty;
    private Integer score;
    private ResultEntity result;
    private MultipartFile file;
    private long challengeID;
    private String markingType;

}
