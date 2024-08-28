package com.ericsson.oko.ingest;

import com.ericsson.oko.dataobject.Alert;
import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.entities.Application;
import com.ericsson.oko.entities.Repository;
import com.ericsson.oko.entities.RepositoryRepo;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SummaryEngine {

    @Autowired
    RepositoryRepo repositoryRepo;

    @Autowired
    private JenkinsHandler handler;

    private static class AlertConstants {
        public static final String OVER_HALF_FAILURES = "Build failure rate over 50%";
        public static final String OLD_BUILD = "Last build more than seven days ago";
        public static final String NEVER_BUILT = "Failed to find build data for this job";
    }

    private static final List<Function<Summary, Alert>> alertRules = Arrays.asList(
            (s) -> s.getFailCount()*1.0 / s.getBuildCount() * 1.0 > 0.5 ? new Alert("50% Fail", AlertConstants.OVER_HALF_FAILURES, s.getRepository()) : null,
            (s) -> s.getLastBuilt().isBefore(LocalDateTime.now().minusDays(7)) ? new Alert("Old Build", AlertConstants.OLD_BUILD, s.getRepository()) : null,
            (s) -> s.getBuildCount() > 0 ? null : new Alert("No Builds", AlertConstants.NEVER_BUILT, s.getRepository())
    );

    public Summary getSummary(Repository r) throws IOException {
        var builds = handler.getBuilds(r.getJobName());
        var s = new Summary(builds, r);
        s.setAlerts(getSummaryAlerts(s));
        return s;
    }

    public List<Summary> getReport(Application a) throws IOException {
        return repositoryRepo.getListByProperty("applicationId", a.getId()).stream()
                .map(r -> {
                    try {return getSummary(r); }
                    catch (IOException e) {throw new RuntimeException(e);}
                })
                .collect(Collectors.toList());
    }

    private List<Alert> getSummaryAlerts(Summary s){
        return alertRules.stream()
                .map(rule->rule.apply(s))
                .filter(a -> a != null)
                .collect(Collectors.toList());
    }

    public List<Alert> getAlerts(Repository r) throws IOException {
        var summary = getSummary(r);
        return getSummaryAlerts(summary);
    }

    public List<Alert> getAlerts(Application a) throws IOException {
        return getReport(a).stream().flatMap(s -> getSummaryAlerts(s).stream())
                .collect(Collectors.toList());
    }
}
