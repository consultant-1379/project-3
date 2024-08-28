package com.ericsson.oko.ingest;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JenkinsHandler {

    public static final String JENKINS_SERVER_URL = "https://fem106-eiffel004.lmera.ericsson.se:8443/jenkins/";

    private JenkinsServer server;

    private CacheManager<String, List<BuildWithDetails>> buildDetailsCache;

    private final long CACHE_SECONDS_TO_LIVE = 600;

    public JenkinsHandler(){
        try {
            server = new JenkinsServer(new URI(JENKINS_SERVER_URL));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);//If we can't reach this server oko dies. I'm fine with that I think?
        }
        buildDetailsCache = new CacheManager<>(CACHE_SECONDS_TO_LIVE);
    }

    public List<BuildWithDetails> getBuilds(String jobName) throws IOException {
        var job = server.getJob(jobName);
        if(job == null) return null;
        var builds = job.getBuilds();
        if(builds.size() < 1) return new ArrayList<>();
        if(buildDetailsCache.has(jobName)) return buildDetailsCache.get(jobName);
        List<BuildWithDetails> buildDetails = new ArrayList<>();
        for (Build build : builds) {
            BuildWithDetails detail = build.details();
            buildDetails.add(detail);
        }
        buildDetailsCache.set(jobName, buildDetails);
        return buildDetails;

    }

    public boolean jobExists(String job) {
        try {
            return  server.getJob(job) != null;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> getJobNameSuggestions(String seg) throws IOException {
        return server.getJobs().keySet().stream()
                .filter(s -> s.toLowerCase().contains(seg.toLowerCase())).collect(Collectors.toList());
    }
}
