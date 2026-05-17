package ke.co.bungesummary.config;

import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.repository.SittingRepository;
import ke.co.bungesummary.ingestion.batch.ProceedingItemProcessor;
import ke.co.bungesummary.ingestion.batch.ProceedingItemWriter;
import ke.co.bungesummary.ingestion.batch.SittingIngestionService;
import ke.co.bungesummary.ingestion.batch.SittingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnBean(SittingRepository.class)
public class BatchConfig {

    @Bean
    public Job sittingIngestionJob(JobRepository jobRepository, Step ingestSittingsStep) {
        return new JobBuilder("sittingIngestionJob", jobRepository)
                .start(ingestSittingsStep)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Sitting> sittingItemReader(SittingRepository sittingRepository) {
        return new SittingItemReader(sittingRepository);
    }

    @Bean
    public ProceedingItemProcessor proceedingItemProcessor(SittingIngestionService sittingIngestionService) {
        return new ProceedingItemProcessor(sittingIngestionService);
    }

    @Bean
    public ProceedingItemWriter proceedingItemWriter() {
        return new ProceedingItemWriter();
    }

    @Bean
    public Step ingestSittingsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Sitting> sittingItemReader,
            ProceedingItemProcessor proceedingItemProcessor,
            ProceedingItemWriter proceedingItemWriter) {
        return new StepBuilder("ingestSittingsStep", jobRepository)
                .<Sitting, Sitting>chunk(1, transactionManager)
                .reader(sittingItemReader)
                .processor(proceedingItemProcessor)
                .writer(proceedingItemWriter)
                .build();
    }
}
