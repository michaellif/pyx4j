/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Nov 25, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.docs.sheet;

import java.util.HashMap;
import java.util.Map;

/**
 * Delegates all the formating to ReportModelFormatter.
 * Detect changes in a Key of ReportModelTotalFormatter and calls ReportModelTotalFormatter to format total
 *
 * @param <Model>
 * @param <TotalAggregator>
 */
public final class ReportModelFormatterWithTotal<Model, TotalAggregator> implements ReportModelFormatter<Model> {

    private final ReportModelFormatter<Model> modelFormatter;

    private final ReportModelTotalFormatter<Model, TotalAggregator> totalFormatter;

    private final Map<String, TotalAggregator> totals = new HashMap<String, TotalAggregator>();

    private String currentKey = null;

    public ReportModelFormatterWithTotal(ReportModelFormatter<Model> modelFormatter, ReportModelTotalFormatter<Model, TotalAggregator> totalFormatter) {
        this.modelFormatter = modelFormatter;
        this.totalFormatter = totalFormatter;
    }

    @Override
    public void createHeader(ReportTableFormatter formatter) {
        modelFormatter.createHeader(formatter);
    }

    @Override
    public void reportEntity(ReportTableFormatter formatter, Model entity) {
        reportTotalIfKeyChanged(formatter, totalFormatter.getKey(entity));
        modelFormatter.reportEntity(formatter, entity);
        addToTotal(totalFormatter.getKey(entity), entity);
    }

    @Override
    public void createFooter(ReportTableFormatter formatter) {
        modelFormatter.createFooter(formatter);
        reportLastTotal(formatter);
    }

    private void addToTotal(String key, Model entity) {
        TotalAggregator total = totals.get(key);
        total = totalFormatter.totalAdd(total, entity);
        totals.put(key, total);
        currentKey = key;
    }

    private TotalAggregator getTotalAggregator(String key) {
        return totals.get(key);
    }

    private void reportTotalIfKeyChanged(ReportTableFormatter formatter, String key) {
        if ((currentKey != null) && !currentKey.equals(key)) {
            totalFormatter.formatTotal(formatter, currentKey, getTotalAggregator(currentKey));
        }
    }

    private void reportLastTotal(ReportTableFormatter formatter) {
        if (currentKey != null) {
            totalFormatter.formatTotal(formatter, currentKey, getTotalAggregator(currentKey));
            currentKey = null;
        }
    }
}
