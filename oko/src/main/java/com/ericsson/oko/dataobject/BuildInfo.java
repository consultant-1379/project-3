package com.ericsson.oko.dataobject;

import com.offbytwo.jenkins.model.BuildCause;
import com.offbytwo.jenkins.model.BuildChangeSetAuthor;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class BuildInfo {
    private String url;
    private String id;
    private LocalDateTime date;
    private BuildResult result;
    private String builtOn;
    private List<String> causes;
    private List<String> culprits;

    public BuildInfo(){
        //For serialization
    }

    public BuildInfo(BuildWithDetails b){
        this.url = b.getUrl();
        this.id = b.getId();
        this.date = Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.result = b.getResult();
        this.builtOn = b.getBuiltOn();
        this.causes = b.getCauses().stream().map(BuildCause::getShortDescription).collect(Collectors.toList());
        this.culprits = b.getCulprits().stream().map(BuildChangeSetAuthor::getAbsoluteUrl).collect(Collectors.toList());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BuildResult getResult() {
        return result;
    }

    public void setResult(BuildResult result) {
        this.result = result;
    }

    public String getBuiltOn() {
        return builtOn;
    }

    public void setBuiltOn(String builtOn) {
        this.builtOn = builtOn;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public List<String> getCulprits() {
        return culprits;
    }

    public void setCulprits(List<String> culprits) {
        this.culprits = culprits;
    }

    @Override
    public String toString() {
        return "BuildInfo{" +
                "url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", date=" + date +
                ", result=" + result +
                ", builtOn='" + builtOn + '\'' +
                ", causes=" + causes +
                ", culprits=" + culprits +
                '}';
    }
}
