package com.ericsson.oko.dataobject;

import com.ericsson.oko.entities.Repository;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Summary {

    private long buildCount;
    private LocalDateTime lastBuilt;
    private long failCount;
    private long passCount;
    private Repository repository;
    private List<BuildInfo> builds;
    private List<Alert> alerts;//It makes sense to generate this list while we have the summary anyway

    public List<BuildInfo> getBuilds() {
        return builds;
    }

    public void setBuilds(List<BuildInfo> builds) {
        this.builds = builds;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public Summary(List<BuildWithDetails> builds, Repository r, List<Alert> alerts) {
        this.buildCount = builds.stream().count();
        if(buildCount != 0) {
            this.lastBuilt =
                    Instant.ofEpochMilli(
                            builds.stream().mapToLong(b -> b.getTimestamp()).max().getAsLong()
                    ).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }else {
            this.lastBuilt = LocalDateTime.now();
        }
        this.failCount = builds.stream().filter(b -> b.getResult() != BuildResult.SUCCESS).count();
        this.passCount = buildCount - failCount;
        this.repository = r;
        this.builds = builds.stream().map(BuildInfo::new).collect(Collectors.toList());
        this.alerts = alerts;
    }

    public Summary(List<BuildWithDetails> builds, Repository r){
        this(builds, r, new ArrayList<>());
    }

    public Summary(){
        //default constructor for jackson data bind
    }

    public long getBuildCount() {
        return buildCount;
    }

    public void setBuildCount(long buildCount) {
        this.buildCount = buildCount;
    }

    public LocalDateTime getLastBuilt() {
        return lastBuilt;
    }

    public void setLastBuilt(LocalDateTime lastBuilt) {
        this.lastBuilt = lastBuilt;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    public long getPassCount() {
        return passCount;
    }

    public void setPassCount(long passCount) {
        this.passCount = passCount;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "buildCount=" + buildCount +
                ", lastBuilt=" + lastBuilt +
                ", failCount=" + failCount +
                ", passCount=" + passCount +
                ", repository=" + repository +
                '}';
    }
}
