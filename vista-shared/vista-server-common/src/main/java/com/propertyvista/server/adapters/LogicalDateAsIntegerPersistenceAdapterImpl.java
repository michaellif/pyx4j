/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.shared.adapters.LogicalDateAsIntegerPersistenceAdapter;

public class LogicalDateAsIntegerPersistenceAdapterImpl implements LogicalDateAsIntegerPersistenceAdapter {

    @Override
    public Class<Integer> getDatabaseType() {
        return Integer.class;
    }

    @Override
    public Class<LogicalDate> getValueType() {
        return LogicalDate.class;
    }

    @Override
    public Integer persist(LogicalDate value) {
        if (value == null) {
            return null;
        } else {
            return (int) (value.getTime() / Consts.DAY2MSEC);
        }
    }

    @Override
    public LogicalDate retrieve(Integer databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            return new LogicalDate(Consts.DAY2MSEC * databaseValue.intValue());
        }
    }
}
