/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.maintenance;

import java.util.Date;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;

@Transient
@ToStringFormat("{0} ({1})")
public interface MaintenanceRequestStatusDTO extends IEntity {

    @ToString(index = 0)
    IPrimitive<String> subject();

    IPrimitive<String> description();

    @ToString(index = 1)
    MaintenanceRequestStatus status();

    MaintenanceRequestPriority priority();

    IPrimitive<Date> lastUpdated();
}
