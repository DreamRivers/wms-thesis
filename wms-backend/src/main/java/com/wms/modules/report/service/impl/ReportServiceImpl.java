package com.wms.modules.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.modules.inbound.entity.InboundOrder;
import com.wms.modules.inbound.mapper.InboundOrderMapper;
import com.wms.modules.outbound.entity.OutboundOrder;
import com.wms.modules.outbound.mapper.OutboundOrderMapper;
import com.wms.modules.report.service.ReportService;
import com.wms.modules.stock.entity.Stock;
import com.wms.modules.stock.entity.StockRecord;
import com.wms.modules.stock.mapper.StockMapper;
import com.wms.modules.stock.mapper.StockRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InboundOrderMapper inboundMapper;
    private final OutboundOrderMapper outboundMapper;
    private final StockMapper stockMapper;
    private final StockRecordMapper recordMapper;

    @Override
    public Map<String, Object> dashboard() {
        Map<String, Object> map = new HashMap<>();
        // 库存总量
        List<Stock> stocks = stockMapper.selectList(null);
        int totalQty = stocks.stream().mapToInt(Stock::getQuantity).sum();
        // 今日入库
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        long todayIn = inboundMapper.selectCount(new LambdaQueryWrapper<InboundOrder>()
                .ge(InboundOrder::getCreateTime, todayStart).lt(InboundOrder::getCreateTime, todayEnd)
                .eq(InboundOrder::getStatus, "FINISHED"));
        long todayOut = outboundMapper.selectCount(new LambdaQueryWrapper<OutboundOrder>()
                .ge(OutboundOrder::getCreateTime, todayStart).lt(OutboundOrder::getCreateTime, todayEnd)
                .eq(OutboundOrder::getStatus, "SHIPPED"));
        long warningCount = stocks.stream()
                .filter(s -> s.getQuantity() < 10).count();

        map.put("totalStock", totalQty);
        map.put("todayIn", todayIn);
        map.put("todayOut", todayOut);
        map.put("warningCount", warningCount);
        map.put("stockSkuCount", stocks.size());
        return map;
    }

    @Override
    public Object inboundTrend(String startDate, String endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(6).toString();
        if (endDate == null) endDate = LocalDate.now().toString();
        LocalDate startD = LocalDate.parse(startDate);
        LocalDate endD = LocalDate.parse(endDate);
        LocalDateTime start = startD.atStartOfDay();
        LocalDateTime end = endD.plusDays(1).atStartOfDay();

        List<InboundOrder> list = inboundMapper.selectList(
                new LambdaQueryWrapper<InboundOrder>()
                        .ge(InboundOrder::getCreateTime, start)
                        .lt(InboundOrder::getCreateTime, end)
                        .eq(InboundOrder::getStatus, "FINISHED"));

        // 按天聚合
        Map<String, Integer> map = new TreeMap<>();
        for (InboundOrder o : list) {
            String day = o.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            map.merge(day, o.getTotalQty() == null ? 0 : o.getTotalQty(), Integer::sum);
        }
        // 补全 0 占位 (X 轴完整)
        List<String> dates = new ArrayList<>();
        List<Integer> qtys = new ArrayList<>();
        for (LocalDate d = startD; !d.isAfter(endD); d = d.plusDays(1)) {
            String key = d.toString();
            dates.add(key);
            qtys.add(map.getOrDefault(key, 0));
        }
        return Map.of("dates", dates, "qtys", qtys);
    }

    @Override
    public Object outboundTrend(String startDate, String endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(6).toString();
        if (endDate == null) endDate = LocalDate.now().toString();
        LocalDate startD = LocalDate.parse(startDate);
        LocalDate endD = LocalDate.parse(endDate);
        LocalDateTime start = startD.atStartOfDay();
        LocalDateTime end = endD.plusDays(1).atStartOfDay();

        List<OutboundOrder> list = outboundMapper.selectList(
                new LambdaQueryWrapper<OutboundOrder>()
                        .ge(OutboundOrder::getCreateTime, start)
                        .lt(OutboundOrder::getCreateTime, end)
                        .eq(OutboundOrder::getStatus, "FINISHED"));

        Map<String, Integer> map = new TreeMap<>();
        for (OutboundOrder o : list) {
            String day = o.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            map.merge(day, o.getTotalQty() == null ? 0 : o.getTotalQty(), Integer::sum);
        }
        // 补全 0 占位
        List<String> dates = new ArrayList<>();
        List<Integer> qtys = new ArrayList<>();
        for (LocalDate d = startD; !d.isAfter(endD); d = d.plusDays(1)) {
            String key = d.toString();
            dates.add(key);
            qtys.add(map.getOrDefault(key, 0));
        }
        return Map.of("dates", dates, "qtys", qtys);
    }

    @Override
    public Object inventoryValue() {
        List<Stock> stocks = stockMapper.selectList(null);
        Map<String, Integer> typeMap = new HashMap<>();
        typeMap.put("正常", 0);
        typeMap.put("锁定", 0);
        for (Stock s : stocks) {
            typeMap.merge("正常", s.getQuantity() == null ? 0 : s.getQuantity(), Integer::sum);
            typeMap.merge("锁定", s.getLockedQty() == null ? 0 : s.getLockedQty(), Integer::sum);
        }
        return typeMap;
    }

    @Override
    public Object topGoods(String type) {
        // 按商品聚合出库数量
        List<StockRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<StockRecord>()
                        .eq("OUTBOUND".equals(type), StockRecord::getBusinessType, "OUTBOUND"));
        Map<Long, Integer> map = new HashMap<>();
        for (StockRecord r : records) {
            map.merge(r.getGoodsId(), r.getChangeQty() == null ? 0 : r.getChangeQty(), Integer::sum);
        }
        List<Map.Entry<Long, Integer>> top = map.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        return top;
    }
}
