package com.cirt.ctf.team;

import com.cirt.ctf.payload.IncrementalScore;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.user.User;
import com.cirt.ctf.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
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

    @JsonIgnore
    private List<User> members;

    private List<SubmissionEntity> submissions;

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
        return submissions.stream().mapToInt(sub->{
            if(sub.getResult()==null)
                return 0;
            else
                return sub.getResult().getScore();
        }).sum();
    }

    public LocalDateTime getLastSubmissionTime(){
        Optional<SubmissionEntity> entity= submissions.stream().max(Comparator.comparing(SubmissionEntity::getSubmissionTime));
        return entity.map(SubmissionEntity::getSubmissionTime).orElse(LocalDateTime.MAX);
    }

    public List<IncrementalScore> getIncrementalScore(){
        List<IncrementalScore> list= new ArrayList<>();
        List<SubmissionEntity> orderSubmissions= new ArrayList<>(submissions).stream().sorted(Comparator.comparing(SubmissionEntity::getSubmissionTime)).toList();
        int gradualScore=0;
        for(SubmissionEntity e: orderSubmissions){
            if(e.getResult()!=null){
                gradualScore+=e.getResult().getScore();
                IncrementalScore ic= IncrementalScore.builder()
                        .score(gradualScore)
                        .time(Utils.sdf.format(e.getSubmissionTime())).build();
                list.add(ic);

            }
        }

        LocalDateTime temp= LocalDateTime.now();

        IncrementalScore s1=IncrementalScore.builder()
                .score(10)
                .time(temp.minusHours(1).format(Utils.formatter)).build();
        IncrementalScore s2=IncrementalScore.builder()
                .score(12)
                .time(temp.plusHours(1).format(Utils.formatter)).build();
        list.add(s1);
        list.add(s2);
        return list;

    }
}