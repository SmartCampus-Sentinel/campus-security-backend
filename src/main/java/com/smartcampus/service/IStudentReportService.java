package com.smartcampus.service;

import com.smartcampus.dto.AuditReportDto;
import com.smartcampus.dto.StudentReportDto;
import com.smartcampus.entity.StudentReport;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IStudentReportService extends IService<StudentReport> {

    StudentReport submit(StudentReportDto dto);

    void audit(AuditReportDto dto);
}
