package com.example.kickunity.Team;

public class AddTeamRequest {
    private String teamName;
    private String teamCategory;
    private String teamStartDate;
    private String teamRegion;
    private String teamAge;
    private int teamSize;
    private String teamDescription;

    public AddTeamRequest(String teamName, String teamCategory, String teamStartDate, String teamRegion, String teamAge, int teamSize, String teamDescription) {
        this.teamName = teamName;
        this.teamCategory = teamCategory;
        this.teamStartDate = teamStartDate;
        this.teamRegion = teamRegion;
        this.teamAge = teamAge;
        this.teamSize = teamSize;
        this.teamDescription = teamDescription;
    }

    // Getter와 Setter
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamCategory() { return teamCategory; }
    public void setTeamCategory(String teamCategory) { this.teamCategory = teamCategory; }

    public String getTeamStartDate() { return teamStartDate; }
    public void setTeamStartDate(String teamStartDate) { this.teamStartDate = teamStartDate; }

    public String getTeamRegion() { return teamRegion; }
    public void setTeamRegion(String teamRegion) { this.teamRegion = teamRegion; }

    public String getTeamAge() { return teamAge; }
    public void setTeamAge(String teamAge) { this.teamAge = teamAge; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public String getTeamDescription() { return teamDescription; }
    public void setTeamDescription(String teamDescription) { this.teamDescription = teamDescription; }
}
