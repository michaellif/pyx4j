/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;

public abstract class ExportTotals<T, DTO> {

    private final Map<String, T> totals = new HashMap<String, T>();

    private String currentKey = null;

    public void addToTotal(String key, DTO entity) {
        T total = totals.get(key);
        total = add(total, entity);
        totals.put(key, total);
        currentKey = key;
    }

    public T get(String key) {
        return totals.get(key);
    }

    protected abstract T add(T total, DTO entity);

    protected abstract void exportTotal(ReportTableXLSXFormatter formatter, String key, T total);

    public void reportTotalIfKeyChanged(ReportTableXLSXFormatter formatter, String key) {
        if ((currentKey != null) && !currentKey.equals(key)) {
            exportTotal(formatter, currentKey, get(currentKey));
        }
    }

    public void reportLastTotal(ReportTableXLSXFormatter formatter) {
        if (currentKey != null) {
            exportTotal(formatter, currentKey, get(currentKey));
            currentKey = null;
        }
    }

}
