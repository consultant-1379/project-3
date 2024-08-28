package com.ericsson.oko.entities;

import com.ericsson.oko.dtos.RepositoryDTO;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "REPOSITORY")
@Entity
public class Repository {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="JOB_NAME", nullable=false)
    private String jobName;

    @Column(name="GERRIT_URL", nullable=false)
    private String gerritUrl;

    @Column(name="APPLICATION_ID", nullable=false)
    private long applicationId;

    public Repository() {
    }

    public Repository(String jobName, String gerritUrl, long applicationId) {
        this.jobName = jobName;
        this.gerritUrl = gerritUrl;
        this.applicationId = applicationId;
    }

    public void mergeWithDTO(RepositoryDTO data){
        this.applicationId = data.getApplicationId() == null ? this.applicationId : data.getApplicationId();
        this.gerritUrl = data.getGerritUrl() == null ? this.gerritUrl : data.getGerritUrl();
        this.jobName = data.getJobName() == null ? this.jobName : data.getJobName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repository)) return false;
        Repository that = (Repository) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getGerritUrl() {
        return gerritUrl;
    }

    public void setGerritUrl(String gerritUrl) {
        this.gerritUrl = gerritUrl;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", jobName='" + jobName + '\'' +
                ", gerritUrl='" + gerritUrl + '\'' +
                ", applicationId=" + applicationId +
                '}';
    }
}
