package com.smartcampus.service.impl;

import com.smartcampus.dto.AuditReportDto;
import com.smartcampus.dto.StudentReportDto;
import com.smartcampus.entity.StudentReport;
import com.smartcampus.mapper.StudentReportMapper;
import com.smartcampus.service.IStudentReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StudentReportServiceImpl extends ServiceImpl<StudentReportMapper, StudentReport> implements IStudentReportService {

    @Override
    public StudentReport submit(StudentReportDto dto) {
        StudentReport report = new StudentReport();
        report.setStudentId(dto.getStudentId());
        report.setReportType(dto.getReportType());
        report.setLocation(dto.getLocation());
        report.setDescription(dto.getDescription());
        report.setMediaUrl(dto.getMediaUrl());
        report.setReportTime(LocalDateTime.now());
        report.setAuditStatus((byte) 0);
        this.save(report);
        return report;
    }

    @Override
    public void audit(AuditReportDto dto) {
        StudentReport report = this.getById(dto.getReportId());
        if (report == null) {
            throw new RuntimeException("上报记录不存在: " + dto.getReportId());
        }
        if (report.getAuditStatus() != 0) {
            throw new RuntimeException("该记录已审核，不可重复操作");
        }
        report.setAuditStatus(dto.getAuditStatus());
        this.updateById(report);
    }
}
