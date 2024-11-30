package com.example.kickunity.Team;

public class TeamSummaryResponse {
    private Long id;
    private String teamName;
    private String teamCategory;
    private String teamRegion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamCategory() { return teamCategory; }
    public void setTeamCategory(String teamCategory) { this.teamCategory = teamCategory; }

    public String getTeamRegion() { return teamRegion; }
    public void setTeamRegion(String teamRegion) { this.teamRegion = teamRegion; }
}