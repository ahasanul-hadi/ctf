package com.cirt.ctf.team;

import com.cirt.ctf.payload.IncrementalScore;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.user.User;
import com.cirt.ctf.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
public class TeamDTO {
    private Long id;
    @NotEmpty(message = "Please enter valid Team Name")
    private String teamName;

    @NotEmpty(message = "Please enter valid Organization Name")
    private String teamOrganization;

    @NotEmpty(message = "Please enter valid OrderID")
    private String orderID;

    @NotEmpty(message = "Please enter valid Payment Email")
    private String paymentEmail;

    private boolean isDisplayed=true;

    private List<SubmissionEntity> submissions;

    @JsonIgnore
    private List<User> members;
    public int getCount(){
        return members.size();
    }

    public boolean isApproved(){
        for(User u: members){
            if(!u.isEnabled())
                return false;
        }
        return true;
    }


    public int getScore(){
        return submissions.stream().mapToInt(sub-> sub.isPublished()?sub.getScore():0).sum();
    }

    public LocalDateTime getLastSubmissionTime(){
        Optional<SubmissionEntity> entity= submissions.stream().filter(sub->sub.isPublished() && sub.getResult().getScore()>0).max(Comparator.comparing(SubmissionEntity::getSubmissionTime));
        return entity.map(SubmissionEntity::getSubmissionTime).orElse(LocalDateTime.MAX);
    }

}
