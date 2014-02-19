package fuse.pocs.camel.spring.properties;

import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;

public class FixedMapJobRepositoryFactoryBean extends MapJobRepositoryFactoryBean {

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        return new FixedMapJobExecutionDao();
    }

}
