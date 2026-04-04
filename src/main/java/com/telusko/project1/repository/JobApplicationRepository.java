package com.telusko.project1.repository;

import com.telusko.project1.model.User;
import com.telusko.project1.model.JobApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {


}
