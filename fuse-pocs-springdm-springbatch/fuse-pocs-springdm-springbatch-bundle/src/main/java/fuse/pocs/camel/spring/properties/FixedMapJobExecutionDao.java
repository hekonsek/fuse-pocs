package fuse.pocs.camel.spring.properties;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.support.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class FixedMapJobExecutionDao implements JobExecutionDao {

    // OSGi-friendly deserialization utility

    public static Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    String name = desc.getName();
                    return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
                }
            }.readObject();
        } catch (OptionalDataException e) {
            throw new IllegalArgumentException("Could not deserialize object: eof=" + e.eof + " at length=" + e.length, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not deserialize object", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not deserialize object type", e);
        }

    }

    // The rest of the copied class starts here

    // JDK6 Make this into a ConcurrentSkipListMap: adds and removes tend to be very near the front or back
    private final ConcurrentMap<Long, JobExecution> executionsById = new ConcurrentHashMap<Long, JobExecution>();

    private final AtomicLong currentId = new AtomicLong(0L);

    public void clear() {
        executionsById.clear();
    }

    private static JobExecution copy(JobExecution original) {
        JobExecution copy = (JobExecution) deserialize(SerializationUtils.serialize(original));
        return copy;
    }

    public void saveJobExecution(JobExecution jobExecution) {
        Long newId = currentId.getAndIncrement();
        jobExecution.setId(newId);
        jobExecution.incrementVersion();
        executionsById.put(newId, copy(jobExecution));
    }

    public List<JobExecution> findJobExecutions(JobInstance jobInstance) {
        List<JobExecution> executions = new ArrayList<JobExecution>();
        for (JobExecution exec : executionsById.values()) {
            if (exec.getJobInstance().equals(jobInstance)) {
                executions.add(copy(exec));
            }
        }
        Collections.sort(executions, new Comparator<JobExecution>() {

            public int compare(JobExecution e1, JobExecution e2) {
                long result = (e1.getId() - e2.getId());
                if (result > 0) {
                    return -1;
                } else if (result < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return executions;
    }

    public void updateJobExecution(JobExecution jobExecution) {
        Long id = jobExecution.getId();
        JobExecution persistedExecution = executionsById.get(id);

        synchronized (jobExecution) {
            if (!persistedExecution.getVersion().equals(jobExecution.getVersion())) {
                throw new RuntimeException("Attempt to update step execution id=" + id
                        + " with wrong version (" + jobExecution.getVersion() + "), where current version is "
                        + persistedExecution.getVersion());
            }
            jobExecution.incrementVersion();
            executionsById.put(id, copy(jobExecution));
        }
    }

    public JobExecution getLastJobExecution(JobInstance jobInstance) {
        JobExecution lastExec = null;
        for (JobExecution exec : executionsById.values()) {
            if (!exec.getJobInstance().equals(jobInstance)) {
                continue;
            }
            if (lastExec == null) {
                lastExec = exec;
            }
            if (lastExec.getCreateTime().before(exec.getCreateTime())) {
                lastExec = exec;
            }
        }
        return copy(lastExec);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.springframework.batch.core.repository.dao.JobExecutionDao#
     * findRunningJobExecutions(java.lang.String)
     */
    public Set<JobExecution> findRunningJobExecutions(String jobName) {
        Set<JobExecution> result = new HashSet<JobExecution>();
        for (JobExecution exec : executionsById.values()) {
            if (!exec.getJobInstance().getJobName().equals(jobName) || !exec.isRunning()) {
                continue;
            }
            result.add(copy(exec));
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.batch.core.repository.dao.JobExecutionDao#getJobExecution
     * (java.lang.Long)
     */
    public JobExecution getJobExecution(Long executionId) {
        return copy(executionsById.get(executionId));
    }

    public void synchronizeStatus(JobExecution jobExecution) {
        JobExecution saved = getJobExecution(jobExecution.getId());
        if (saved.getVersion().intValue() != jobExecution.getVersion().intValue()) {
            jobExecution.upgradeStatus(saved.getStatus());
            jobExecution.setVersion(saved.getVersion());
        }
    }
}