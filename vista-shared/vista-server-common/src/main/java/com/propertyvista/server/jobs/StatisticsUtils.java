/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.StatisticsRecord;

public class StatisticsUtils {

    private static long nvl(Long value) {
        if (value == null) {
            return 0;
        } else {
            return value;
        }
    }

    private static double nvl(Double value) {
        if (value == null) {
            return 0;
        } else {
            return value;
        }
    }

    public static void nvlAddLong(IPrimitive<Long> target, IPrimitive<Long> value) {
        if (target.isNull()) {
            target.setValue(value.getValue());
        } else if (!value.isNull()) {
            target.setValue(target.getValue() + value.getValue());
        }
    }

    public static void nvlAddDouble(IPrimitive<Double> target, IPrimitive<Double> value) {
        if (target.isNull()) {
            target.setValue(value.getValue());
        } else if (!value.isNull()) {
            target.setValue(target.getValue() + value.getValue());
        }
    }

    private static void sumTotals(StatisticsRecord dynamicStatisticsRecord) {
        dynamicStatisticsRecord.total().setValue(nvl(dynamicStatisticsRecord.failed().getValue()) + nvl(dynamicStatisticsRecord.processed().getValue()));
    }

    public static void addProcessed(StatisticsRecord dynamicStatisticsRecord, int count) {
        dynamicStatisticsRecord.processed().setValue(nvl(dynamicStatisticsRecord.processed().getValue()) + count);
        sumTotals(dynamicStatisticsRecord);
    }

    public static void addProcessed(StatisticsRecord dynamicStatisticsRecord, int count, double amount) {
        addProcessed(dynamicStatisticsRecord, count);
        dynamicStatisticsRecord.amountProcessed().setValue(nvl(dynamicStatisticsRecord.amountProcessed().getValue()) + amount);
    }

    public static void addProcessed(StatisticsRecord dynamicStatisticsRecord, int count, BigDecimal amount) {
        addProcessed(dynamicStatisticsRecord, count);
        dynamicStatisticsRecord.amountProcessed().setValue(nvl(dynamicStatisticsRecord.amountProcessed().getValue()) + amount.doubleValue());
    }

    public static void addFailed(StatisticsRecord dynamicStatisticsRecord, int count) {
        dynamicStatisticsRecord.failed().setValue(nvl(dynamicStatisticsRecord.failed().getValue()) + count);
        sumTotals(dynamicStatisticsRecord);
    }

    public static void addFailed(StatisticsRecord dynamicStatisticsRecord, int count, BigDecimal amount) {
        addFailed(dynamicStatisticsRecord, count);
        dynamicStatisticsRecord.amountFailed().setValue(nvl(dynamicStatisticsRecord.amountFailed().getValue()) + amount.doubleValue());
    }
}
