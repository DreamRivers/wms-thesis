package com.wms.framework.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.modules.basic.entity.Goods;
import com.wms.modules.basic.mapper.GoodsMapper;
import com.wms.modules.stock.entity.Notification;
import com.wms.modules.stock.entity.Stock;
import com.wms.modules.stock.mapper.NotificationMapper;
import com.wms.modules.stock.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存预警定时任务
 * - 扫描低于安全库存
 * - 扫描临期商品
 * - 写入通知表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockWarningJob {

    private final StockMapper stockMapper;
    private final GoodsMapper goodsMapper;
    private final NotificationMapper notificationMapper;

    /** 每小时执行一次 */
    @Scheduled(cron = "0 0 * * * ?")
    public void scan() {
        log.info("[库存预警] 定时任务开始");
        scanLowStock();
        scanExpireSoon();
        log.info("[库存预警] 定时任务结束");
    }

    /** 低库存扫描 */
    private void scanLowStock() {
        // 简化实现:加载所有商品和库存,内存对比
        List<Goods> goods = goodsMapper.selectList(null);
        Map<Long, Integer> stockMap = new HashMap<>();
        for (Stock s : stockMapper.selectList(null)) {
            stockMap.merge(s.getGoodsId(), s.getQuantity(), Integer::sum);
        }
        for (Goods g : goods) {
            Integer total = stockMap.getOrDefault(g.getId(), 0);
            if (g.getSafetyStock() != null && total < g.getSafetyStock()) {
                // 去重:同一天同商品只发一次
                Long count = notificationMapper.selectCount(
                        new LambdaQueryWrapper<Notification>()
                                .eq(Notification::getType, "LOW_STOCK")
                                .eq(Notification::getRefId, g.getId())
                                .ge(Notification::getCreateTime, LocalDate.now().atStartOfDay()));
                if (count == 0) {
                    Notification n = new Notification();
                    n.setType("LOW_STOCK");
                    n.setTitle("库存预警:" + g.getGoodsName());
                    n.setContent(String.format("当前库存 %d,安全库存 %d", total, g.getSafetyStock()));
                    n.setRefId(g.getId());
                    n.setRefType("GOODS");
                    n.setTargetRole("WAREHOUSE");
                    n.setReadStatus(0);
                    notificationMapper.insert(n);
                    log.info("[库存预警] 生成低库存通知:{}", g.getGoodsName());
                }
            }
        }
    }

    /** 临期扫描:30 天内到期 */
    private void scanExpireSoon() {
        LocalDate threshold = LocalDate.now().plusDays(30);
        List<Stock> stocks = stockMapper.selectList(
                new LambdaQueryWrapper<Stock>().isNotNull(Stock::getExpireDate));
        for (Stock s : stocks) {
            if (s.getExpireDate() != null && s.getExpireDate().isBefore(threshold)) {
                Long count = notificationMapper.selectCount(
                        new LambdaQueryWrapper<Notification>()
                                .eq(Notification::getType, "EXPIRE_SOON")
                                .eq(Notification::getRefId, s.getId())
                                .ge(Notification::getCreateTime, LocalDate.now().atStartOfDay()));
                if (count == 0) {
                    Goods g = goodsMapper.selectById(s.getGoodsId());
                    if (g == null) continue;
                    Notification n = new Notification();
                    n.setType("EXPIRE_SOON");
                    n.setTitle("临期预警:" + g.getGoodsName());
                    n.setContent(String.format("批次 %s 将在 %s 到期", s.getBatchNo(), s.getExpireDate()));
                    n.setRefId(s.getId());
                    n.setRefType("STOCK");
                    n.setTargetRole("WAREHOUSE");
                    n.setReadStatus(0);
                    notificationMapper.insert(n);
                    log.info("[库存预警] 生成临期通知:{}", g.getGoodsName());
                }
            }
        }
    }
}
