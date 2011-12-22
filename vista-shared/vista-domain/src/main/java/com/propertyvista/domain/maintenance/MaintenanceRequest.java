/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.Tenant;

public interface MaintenanceRequest extends IEntity {

    @Owner
    @Detached
    @ReadOnly
    @Indexed
    Tenant tenant();

    //TODO Add 
    //Building building();

    @Detached
    @ReadOnly
    IssueClassification issueClassification();

    IPrimitive<LogicalDate> submited();

    IPrimitive<MaintenanceRequestStatus> status();

    @Caption(name = "Last Updated")
    IPrimitive<LogicalDate> updated();

    IPrimitive<String> description();

    @EmbeddedEntity
    SurveyResponse surveyResponse();
}
