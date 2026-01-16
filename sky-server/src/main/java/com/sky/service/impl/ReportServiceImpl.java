package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //创建集合用于存放begin到end期间每天的日期


        List<LocalDate> dataList=new ArrayList<>();
        dataList.add(begin);
        while (!begin.equals( end)){
            begin=begin.plusDays(1);
            dataList.add(begin);
        }

        List<Double> turnoverList=new ArrayList<>();
        for (LocalDate date : dataList){
            //查询data日期对应的营业额数据，营业额是订单状态为已完成的金额合计的
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            List<Double> turnoverList = orderMapper.getTurnoverStatistics(beginTime, endTime);
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover==null?0.0:turnover);
            //select ********
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();



    }


    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        //创建集合用于存放begin到end期间每天的日期
        List<LocalDate> dataList=new ArrayList<>();
        dataList.add(begin);
        while(!begin.equals( end)){
            begin=begin.plusDays(1);
            dataList.add(begin);
        }
        ///存放每天的新增用户数量
        List<Integer> newUserList=new ArrayList<>();
        //存放每天总用户数量
        List<Integer> totalUserList=new ArrayList<>();
        for (LocalDate date : dataList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map=new HashMap();
            map.put("end",endTime);

            //总用户数量
            Integer totalUser = userMapper.countByMap(map);

            //新增用户数量
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);

        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {

        //存放begin到end之间的日期
        List<LocalDate> datalist=new ArrayList<>();
        datalist.add(begin);
        while(!begin.equals(end)){
             begin = begin.plusDays(1);
             datalist.add(begin);
        }
        //查询每天的订单总数
        List<Integer> orderCountList=new ArrayList<>();
        //每天的有效订单数
        List<Integer> validOrderCountList=new ArrayList<>();



        //遍历datalist集合，查询每天的有效订单数和订单总数
        for(LocalDate date : datalist){
            //查询每天的订单总数，  select count(id) from orders where order_time >= ? and order_time < ?
            LocalDateTime beginTime=LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderCountList.add(orderCount);

            //查询每天的有效订单数 select count(id) from orders where order_time >= ? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间段内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();



        //计算时间段内的有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }



        return OrderReportVO.builder()
                .dateList(StringUtils.join(datalist, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime EndTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, EndTime);
        //将Dto里面的对象转换为后端返回的Vo对象数据
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String name = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String number = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(name)
                .numberList(number)
                .build();

    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库，获取营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin,LocalTime.MIN),LocalDateTime.of(dateEnd,LocalTime.MAX));

        //通过poi将数据写入到Excel中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("运营数据报表模板.xlsx");

        try {
            //获取报表格文件的sheet页
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //填充数据：时间
            XSSFSheet sheet = excel.getSheet("sheet1");

            //获得第四行
            XSSFRow row=sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //获得第五行
            XSSFRow row4 = sheet.getRow(4);
            row4.getCell(2).setCellValue(businessData.getValidOrderCount());
            row4.getCell(6).setCellValue(businessData.getUnitPrice());
            //填充明细数据

            for (int i=0;i<30;i++){
                LocalDate date = dateBegin.plusDays(i);
//                查询某一天的营业数据
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData1.getTurnover());
                row.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData1.getUnitPrice());
                row.getCell(6).setCellValue(businessData1.getNewUsers());

            }
            // 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}
