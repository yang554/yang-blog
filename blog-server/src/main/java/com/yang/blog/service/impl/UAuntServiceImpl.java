package com.yang.blog.service.impl;

import com.yang.blog.entity.UAuntEntity;
import com.yang.blog.mapper.UAuntMapper;
import com.yang.blog.service.UAuntService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class UAuntServiceImpl implements UAuntService {

    @Autowired
    private UAuntMapper auntMapper;

    @Override
    public RespBean getByALL() {
        List<UAuntEntity> auntEntities = auntMapper.getByALL();
        return RespBean.ok("ok",auntEntities);
    }

    @Override
    public RespBean findNameAll() {
        List<UAuntEntity> auntEntities = auntMapper.findNameAll();
        return RespBean.ok("ok",auntEntities);
    }

    @Override
    public RespBean selectByName(String uName) {
        List<UAuntEntity> auntEntities = auntMapper.selectByName(uName);
        return RespBean.ok("ok",auntEntities);
    }

    @Override
    public Map<String,Object> selectByNameAll(String uName,String uStatus) {
        Map<String,Object> map = new HashMap<>();
        List<UAuntEntity> auntEntities = auntMapper.selectByNameAll(uName,uStatus);
        if(auntEntities.size() == 0){
            return null;
        }else if ("1".equals(auntEntities.get(0).getuStatus())){
            map.put("auntList",auntEntities);
            return map;
        }else {
            try {
                // 使用线性回归
                return predictWithRegression(auntEntities);
            } catch (Exception e) {
                // 回退到加权平均法
                return predictWithWeightedAverage(auntEntities);
            }
        }

    }
    //线性回归
    private Map<String,Object> predictWithRegression(List<UAuntEntity> auntEntities) {
        // 准备数据集
        double[] x = new double[auntEntities.size()];   //X坐标，根据第一次记录与后续记录差值
        double[] y = new double[auntEntities.size()];   //Y坐标，实际周期值
        double[] y2 = new double[auntEntities.size()];  //Y坐标，实际持续值

        long X = 0;
        for (int i = 0; i < auntEntities.size(); i++) {
            String c = auntEntities.get(auntEntities.size()-i-1).getCycle();
            X += Long.parseLong(c.substring(0,c.length()-1));
            x[i] = X;
            y[i] = Long.parseLong(c.substring(0,c.length()-1));
            String d = auntEntities.get(auntEntities.size()-i-1).getDuration();
            y2[i] = Long.parseLong(d.substring(0,d.length()-1));
        }

        // 计算周期回归参数
        double[] regressionCycleParams = calculateLinearRegression(x, y);
        double slopeCycle = regressionCycleParams[0];
        double interceptCycle = regressionCycleParams[1];
        // 预测下一个周期长度
        double cycleLastX = x[x.length - 1] + y[y.length - 1];
        double nextCycleLength = slopeCycle * cycleLastX  + interceptCycle;

        // 计算持续时间回归参数
        double[] regressionDurationParams = calculateLinearRegression(x, y2);
        double slopeDuration = regressionDurationParams[0];
        double interceptDuration = regressionDurationParams[1];
        if(slopeCycle > 0){
            // 预测下一个持续时间长度
            double durationLastX = x[x.length - 1] + y[y.length - 1];
            double nextDurationLength = slopeDuration * durationLastX  + interceptDuration;

            // 应用生理约束,强制回归
            nextCycleLength = applyPhysiologicalConstraints(nextCycleLength);

            // 获取最新来临日期
            LocalDate lastStartDate = LocalDate.parse(auntEntities.get(0).getStartDate());

            String msg = "线性回归算法";

            return resultMap(auntEntities,lastStartDate,nextCycleLength,nextDurationLength,msg);
        }else{
            return predictWithWeightedAverage(auntEntities);
        }
    }
    //权重算法
    private Map<String,Object>  predictWithWeightedAverage(List<UAuntEntity> auntEntities) {
        double cycle = 0;
        double duration = 0;
        double weightSum = 0;

        for (int i = 1; i < auntEntities.size(); i++) {
            String c = auntEntities.get(i).getCycle();
            String d = auntEntities.get(i).getDuration();
            long cycleLength = Long.parseLong(c.substring(0,c.length()-1));
            long durationLength = Long.parseLong(d.substring(0,d.length()-1));
            // 越近的记录权重越高
            double weight = (auntEntities.size()-i) * 1.5;
            cycle += cycleLength * weight;
            duration += durationLength * weight;
            weightSum += weight;
        }

        double nextCycleLength = Math.round(cycle / weightSum);
        double nextDurationLength = Math.round(duration / weightSum);
        // 应用生理约束,强制回归
        nextCycleLength = applyPhysiologicalConstraints(nextCycleLength);

        // 获取最新来临日期
        LocalDate lastStartDate = LocalDate.parse(auntEntities.get(0).getStartDate());

        String msg = "权重算法";

        return resultMap(auntEntities,lastStartDate,nextCycleLength,nextDurationLength,msg);
    }

    //封装预测结果
    private Map<String,Object> resultMap(List<UAuntEntity> auntEntities,LocalDate lastStartDate,double nextCycleLength,double nextDurationLength,String msg){
        Map<String,Object> map = new HashMap<>();
        // 创建格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 计算预测日期
        LocalDate formattedDate = lastStartDate.plusDays(Math.round(nextCycleLength));
        String nextPeriodStartDate = formattedDate.format(formatter);
        LocalDate formattedEndDate = formattedDate.plusDays(Math.round(nextDurationLength));
        String nextPeriodEndDate = formattedEndDate.format(formatter);
        long daysUntilNextPeriod = LocalDate.now().until(formattedDate, ChronoUnit.DAYS);
        map.put("nextPeriodStartDate",nextPeriodStartDate); //开始时间
        map.put("nextPeriodEndDate",nextPeriodEndDate); //结束时间
        map.put("daysUntilNextPeriod",daysUntilNextPeriod); //距离开始时间还有多少天
        map.put("cycleLength",Math.round(nextCycleLength));  //周期长度
        map.put("periodLength",Math.round(nextDurationLength)); //持续时间
        map.put("auntList",auntEntities);
        map.put("msg",msg);
        return map;
    }
    //异常周期纠正
    private double applyPhysiologicalConstraints(double cycleLength) {
        // 正常月经周期范围 (21-35天)
        final int MIN_CYCLE = 21;
        final int MAX_CYCLE = 35;

        if (cycleLength < MIN_CYCLE) {
            return MIN_CYCLE;
        }
        if (cycleLength > MAX_CYCLE) {
            return MAX_CYCLE;
        }
        return cycleLength;
    }

    //计算历史平均周期
    private double calculateAverageCycle(List<UAuntEntity> auntEntities) {
        double total = 0;
        int count = 0;

        for (int i = 1; i < auntEntities.size(); i++) {
            long cycleLength = ChronoUnit.DAYS.between(LocalDate.parse(auntEntities.get(i).getStartDate()),LocalDate.parse(auntEntities.get(i-1).getStartDate()));
            total += cycleLength;
            count++;
        }
        return count > 0 ? total / count : 28; // 默认28天
    }

    // 计算线性回归参数（最小二乘法）
    private double[] calculateLinearRegression(double[] x, double[] y) {
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumXX += x[i] * x[i];
        }
        double meanX = sumX / n;
        double meanY = sumY / n;
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = meanY - slope * meanX;

        return new double[]{slope, intercept};
    }

    @Override
    public RespBean addAunt(HashMap<String,Object> entity) {
        UAuntEntity auntEntity = new UAuntEntity();
        auntEntity.setId(CommonDate.getStamp());
        auntEntity.setuID(Long.valueOf(String.valueOf(entity.get("uID"))));
        if("".equals(entity.get("startDate"))){
            auntEntity.setStartDate(null);
        }else {
            auntEntity.setStartDate((String) entity.get("startDate"));
        }
        if("".equals(entity.get("endDate"))){
            auntEntity.setEndDate(null);
        }else {
            auntEntity.setEndDate((String) entity.get("endDate"));
        }
        auntEntity.setBloodVolume((String) entity.get("bloodVolume"));
        auntEntity.setCycle((String) entity.get("cycle"));
        auntEntity.setDuration((String) entity.get("duration"));
        auntEntity.setSymptom((String) entity.get("symptom"));
        auntEntity.setNote((String) entity.get("note"));
        auntEntity.setuStatus((String) entity.get("uStatus"));

        int status = auntMapper.addAunt(auntEntity);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean updAunt(HashMap<String, Object> entity) {
        UAuntEntity auntEntity = new UAuntEntity();
        if("".equals(entity.get("startDate"))){
            auntEntity.setStartDate(null);
        }else {
            auntEntity.setStartDate((String) entity.get("startDate"));
        }
        if( entity.get("endDate") == null || "".equals(entity.get("endDate"))){
            auntEntity.setEndDate(null);
            auntEntity.setuStatus("1");
        }else {
            auntEntity.setEndDate((String) entity.get("endDate"));
            auntEntity.setuStatus("0");
        }

        auntEntity.setBloodVolume((String) entity.get("bloodVolume"));
        auntEntity.setCycle((String) entity.get("cycle"));
        auntEntity.setDuration((String) entity.get("duration"));
        auntEntity.setSymptom((String) entity.get("symptom"));
        auntEntity.setNote((String) entity.get("note"));
        auntEntity.setId((Long) entity.get("id"));

        int status = auntMapper.updAunt(auntEntity);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean delAunt(String id) {
        int status = auntMapper.delAunt(id);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }
}
