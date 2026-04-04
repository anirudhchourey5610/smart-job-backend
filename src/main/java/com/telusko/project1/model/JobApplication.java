package com.telusko.project1.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job_applications")
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String jobRole;
    private String hrEmail;
    private String resumeFileName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
