package com.cirt.ctf.scoreboard;

import java.time.LocalDateTime;

public interface ScoreSummary {
    Long getId();
    String getTeamName();
    String getTeamOrganization();
    Integer getScore();
    LocalDateTime getMaxSubmissionTime();
}
