/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 20, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface AptUnitOccupancyIO extends IEntity {

    IPrimitive<LogicalDate> dateFrom();

    IPrimitive<LogicalDate> dateTo();

    IPrimitive<Status> status();

    IPrimitive<OffMarketType> offMarket();

}
