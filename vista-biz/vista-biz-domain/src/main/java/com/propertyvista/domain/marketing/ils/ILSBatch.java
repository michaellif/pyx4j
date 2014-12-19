/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 24, 2013
 * @author stanp
 */
package com.propertyvista.domain.marketing.ils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;

public interface ILSBatch extends IEntity {
    IPrimitive<LogicalDate> runDate();

    IPrimitive<ILSVendor> vendor();

    @Detached
    @ReadOnly
    @Indexed
    Building building();

    @Detached
    @ReadOnly
    @Indexed
    IList<AptUnit> units();

    IPrimitive<String> listingXml();
}
