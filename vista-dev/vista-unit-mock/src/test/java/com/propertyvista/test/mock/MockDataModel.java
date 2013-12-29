/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.gwt.server.DateUtils;

public abstract class MockDataModel<E extends IEntity> {

    private MockManager mockManager;

    private final List<E> items;

    protected abstract void generate();

    public MockDataModel() {
        items = new ArrayList<E>();
    }

    public void setMockManager(MockManager mockManager) {
        this.mockManager = mockManager;
    }

    public MockConfig getConfig() {
        return mockManager.getConfig();
    }

    public <T extends MockDataModel<?>> T getDataModel(Class<T> modelClass) {
        return mockManager.getDataModel(modelClass);
    }

    public void addItem(E item) {
        items.add(item);
    }

    public List<E> getAllItems() {
        return items;
    }

    public E getItem(int index) {
        return items.get(index);
    }

    protected static LogicalDate parseDate(String date) {
        if (date == null) {
            return null;
        }
        return new LogicalDate(DateUtils.detectDateformat(date));
    }

    protected static Date getSysDate() {
        return SystemDateManager.getDate();
    }
}
