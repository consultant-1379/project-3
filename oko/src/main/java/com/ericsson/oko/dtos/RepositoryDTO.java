package com.ericsson.oko.dtos;

public class RepositoryDTO {
    private String gerritUrl = null;
    private String jobName = null;
    private Long applicationId = null;

    public RepositoryDTO(){
        this.gerritUrl = null;
        this.jobName = null;
        this.applicationId = null;
    }

    @Override
    public String toString() {
        return "RepositoryDTO{" +
                "gerritUrl='" + gerritUrl + '\'' +
                ", jobName='" + jobName + '\'' +
                ", applicationId=" + applicationId +
                '}';
    }

    public String getGerritUrl() {
        return gerritUrl;
    }

    public void setGerritUrl(String gerritUrl) {
        this.gerritUrl = gerritUrl;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
