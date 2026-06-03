package com.wms.modules.report.service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> dashboard();
    Object inboundTrend(String startDate, String endDate);
    Object outboundTrend(String startDate, String endDate);
    Object inventoryValue();
    Object topGoods(String type);
}
