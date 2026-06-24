package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcampus.common.Result;
import com.smartcampus.dto.AuditReportDto;
import com.smartcampus.dto.StudentReportDto;
import com.smartcampus.entity.StudentReport;
import com.smartcampus.service.IStudentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/studentReport")
@RequiredArgsConstructor
public class StudentReportController {

    private final IStudentReportService studentReportService;

    @PostMapping
    public Result<StudentReport> submit(@Valid @RequestBody StudentReportDto dto) {
        StudentReport report = studentReportService.submit(dto);
        return Result.success(report, "上报成功");
    }

    @GetMapping("/page")
    public Result<Page<StudentReport>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Byte auditStatus) {
        Page<StudentReport> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StudentReport> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(StudentReport::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(StudentReport::getReportTime);
        return Result.success(studentReportService.page(page, wrapper));
    }

    @GetMapping("/{reportId}")
    public Result<StudentReport> getById(@PathVariable Long reportId) {
        StudentReport report = studentReportService.getById(reportId);
        if (report == null) {
            return Result.error("上报记录不存在");
        }
        return Result.success(report);
    }

    @PutMapping("/audit")
    public Result<Void> audit(@Valid @RequestBody AuditReportDto dto) {
        studentReportService.audit(dto);
        return Result.success(null, "审核完成");
    }

    @GetMapping("/my/{studentId}")
    public Result<List<StudentReport>> myReports(@PathVariable String studentId) {
        LambdaQueryWrapper<StudentReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentReport::getStudentId, studentId);
        wrapper.orderByDesc(StudentReport::getReportTime);
        return Result.success(studentReportService.list(wrapper));
    }
}
