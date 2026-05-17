package ke.co.bungesummary.ingestion.scheduler;

import java.time.LocalDate;
import ke.co.bungesummary.ingestion.downloader.HansardDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(name = "sittingIngestionJob")
@ConditionalOnProperty(name = "hansard.ingestion.scheduler-enabled", havingValue = "true")
public class HansardIngestionScheduler {

    private static final Logger log = LoggerFactory.getLogger(HansardIngestionScheduler.class);

    private final HansardDownloader hansardDownloader;
    private final JobLauncher jobLauncher;
    private final Job sittingIngestionJob;

    public HansardIngestionScheduler(
            HansardDownloader hansardDownloader,
            JobLauncher jobLauncher,
            Job sittingIngestionJob) {
        this.hansardDownloader = hansardDownloader;
        this.jobLauncher = jobLauncher;
        this.sittingIngestionJob = sittingIngestionJob;
    }

    @Scheduled(cron = "${hansard.ingestion.cron}", zone = "Africa/Nairobi")
    public void runDailyIngestion() throws Exception {
        log.info("Starting scheduled Hansard ingestion");
        LocalDate today = LocalDate.now();
        var urls = hansardDownloader.discoverPdfUrls(today.minusDays(7), today);
        log.info("Discovered {} Hansard PDF URLs", urls.size());

        jobLauncher.run(
                sittingIngestionJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters());
    }
}
