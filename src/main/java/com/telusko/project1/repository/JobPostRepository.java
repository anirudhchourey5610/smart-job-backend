package com.telusko.project1.repository;

import com.telusko.project1.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost,Long> {
    boolean existsByExternalId(Long externalId);
}
