/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.kijiji.mapper.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.marketing.ils.ILSSummary;
import com.propertyvista.domain.property.asset.Floorplan;

@Transient
public interface ILSFloorplanDTO extends IEntity {

    Floorplan floorplan();

    ILSSummary ilsSummary();

    ILSProfileFloorplan profile();

    IPrimitive<Boolean> isFurnished();

    IPrimitive<Boolean> isPetsAllowed();

    IPrimitive<BigDecimal> minPrice();
}
