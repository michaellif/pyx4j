/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.SurveyResponse;

@Transient
public interface MaintananceDTO extends IEntity {

    IPrimitive<String> description();

    @Format("MMM dd, yyyy")
    IPrimitive<LogicalDate> date();

    IPrimitive<MaintenanceRequestStatus> status();

    @EmbeddedEntity
    SurveyResponse satisfactionSurvey();
}
