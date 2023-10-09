package com.cirt.ctf.team;

import com.cirt.ctf.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

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
}
