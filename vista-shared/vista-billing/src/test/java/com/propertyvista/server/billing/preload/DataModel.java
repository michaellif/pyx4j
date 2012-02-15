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
package com.propertyvista.server.billing.preload;

import java.util.List;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

public interface DataModel {

    <T extends IEntity> T retreive(EntityQueryCriteria<T> criteria);

    <T extends IEntity> List<T> query(Class<T> type);

    <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria);

    <T extends IEntity> void persist(T entity);

    <T extends IEntity> void persist(List<T> entityList);

}
