package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> getTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询营业额数据：{}到{}", begin, end);

        return Result.success(reportService.getTurnoverStatistics(begin,end));
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询用户数据：{}到{}", begin, end);

        return Result.success(reportService.getUserStatistics(begin,end));


    }

    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrdersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询订单数据：{}到{}", begin, end);

        return Result.success(reportService.getOrdersStatistics(begin,end));


    }

}
