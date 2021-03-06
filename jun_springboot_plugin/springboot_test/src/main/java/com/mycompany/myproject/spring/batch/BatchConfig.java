package com.mycompany.myproject.spring.batch;


import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

/*
select * from spring_batch_job_instance t ;
select * from spring_batch_job_seq t ;

select * from spring_batch_job_execution t ;
select * from spring_batch_job_execution_seq t;
select * from spring_batch_job_execution_context ;
select * from spring_batch_job_execution_params t;

select * from spring_batch_step_execution t;
select * from spring_batch_step_execution_seq t;
select * from spring_batch_step_execution_context t;
 */

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration batchAutoConfiguration = null;

    // DefaultJobParametersValidator

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    OperaResourceStorageTasklet operaResourceStorageTasklet;

    @Autowired
    XsdValidationTasklet xsdValidationTasklet;

    @Autowired
    MyItemReader myItemReader;

    @Autowired
    MyItemProcesser myItemProcesser;

    @Autowired
    MyWriter myWriter;

    @Bean
    public Job jobImportFromOpera() {
        return jobBuilderFactory.get("jobImportFromOpera")
                .start(storeOperaFeedContents())
                .next(vaildateOperaXml())
                .next(parseOperaBookings())
                .build();
    }

    @Bean
    public Step storeOperaFeedContents() {
        Step step = stepBuilderFactory.get("storeOperaFeedContents")
                .tasklet(operaResourceStorageTasklet)
                //.transactionManager()
                .build();
        return step;
    }

    @Bean
    public Step vaildateOperaXml() {
        Step step = stepBuilderFactory.get("vaildateOperaXml")
                .tasklet(xsdValidationTasklet)
                .build();

        return step;
    }

    @Bean
    public Step parseOperaBookings() {
        StepBuilder stepBuilder = stepBuilderFactory.get("parseOperaBookings");
        SimpleStepBuilder simpleStepBuilder =  stepBuilder.chunk(1000);
        simpleStepBuilder.reader(myItemReader);
        simpleStepBuilder.processor(myItemProcesser);
        simpleStepBuilder.writer(myWriter);

        simpleStepBuilder.listener(new StepExecutionListener(){

            @Override
            public void beforeStep(StepExecution stepExecution) {

            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return null;
            }
        });


        simpleStepBuilder.faultTolerant().skip(SkippableException.class);

        Step step = simpleStepBuilder.build();

        return step;
    }


    ////////////////////////////////////////
    @Bean
    public ItemReader<Person> reader() throws Exception {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>(); //??????FlatFileItemReader????????????
        reader.setResource(new ClassPathResource("batch/people.csv")); //??????FlatFileItemReader???setResource????????????CSV???????????????
        reader.setLineMapper(new DefaultLineMapper<Person>() {{ //????????????CVS????????????????????????????????????????????????
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "name","age", "nation" ,"address"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ItemProcessor<Person, Person> processor() {
        CsvItemProcessor processor = new CsvItemProcessor(); //??????????????????ItemProcessor?????????
        processor.setValidator(csvBeanValidator()); //???Processor???????????????
        return processor;
    }



    @Bean
    public ItemWriter<Person> writer() {//Spring????????????????????????Bean???????????????????????????Spring boot???????????????DataSource

        ItemWriter<Person> itemWriter = new ItemWriter<Person>() {
            @Override
            public void write(List<? extends Person> items) throws Exception {

                for(Person item : items){
                    System.out.println(item);
                }

            }
        };

        return itemWriter;
    }

    @Bean("importJob")
    public Job importJob(JobBuilderFactory jobs, Step step1) {
        return jobs.get("importJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(step1) //??????step
//                .end()
                .start(step1)
                .listener(csvJobListener()) //???????????????
                .build();
    }

    @Bean("step1")
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Person> reader, ItemWriter<Person> writer,
                      ItemProcessor<Person,Person> processor) {
        return stepBuilderFactory
                .get("step1")
                .<Person, Person>chunk(3) //?????????????????????65000?????????
                .reader(reader) //???step??????reader
                .processor(processor) //???step??????Processor
                .writer(writer) //???step??????writer
                .build();
    }

    @Bean
    public CsvJobListener csvJobListener() {
        return new CsvJobListener();
    }

    @Bean
    public Validator<Person> csvBeanValidator() {
        return new CsvBeanValidator<Person>();
    }

}
