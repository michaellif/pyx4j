/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertvista.generator.II;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

public class InMemoryDataModel implements DataModel {

    private final HashMap<Class<? extends IEntity>, List<? extends IEntity>> map;

    public InMemoryDataModel() {
        map = new HashMap<Class<? extends IEntity>, List<? extends IEntity>>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> void persist(T entity) {
        if (!map.containsKey(entity.getObjectClass())) {
            map.put(entity.getObjectClass(), new ArrayList<T>());
        }
        ((List<T>) map.get(entity.getObjectClass())).add(entity);
    }

    @Override
    public <T extends IEntity> void persist(List<T> entityList) {
        for (T entity : entityList) {
            persist(entity);
        }
    }

    @Override
    public <T extends IEntity> T retreive(EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> List<T> query(Class<T> type) {
        return (List<T>) map.get(type);
    }

}
