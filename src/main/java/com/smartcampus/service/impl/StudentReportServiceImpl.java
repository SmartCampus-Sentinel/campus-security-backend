package com.smartcampus.service.impl;

import com.smartcampus.entity.StudentReport;
import com.smartcampus.mapper.StudentReportMapper;
import com.smartcampus.service.IStudentReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学生隐患上报表 服务实现类
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Service
public class StudentReportServiceImpl extends ServiceImpl<StudentReportMapper, StudentReport> implements IStudentReportService {

}
