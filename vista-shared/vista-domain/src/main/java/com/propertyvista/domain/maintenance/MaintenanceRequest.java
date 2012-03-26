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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.Tenant;

public interface MaintenanceRequest extends IEntity {

    @Owner
    @Detached
    @NotNull
    @JoinColumn
    Tenant tenant();

    //TODO Add 
    //Building building();

    @Detached
    @NotNull
    IssueClassification issueClassification();

    IPrimitive<LogicalDate> submitted();

    @NotNull
    IPrimitive<MaintenanceRequestStatus> status();

    @Caption(name = "Last Updated")
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<LogicalDate> updated();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @EmbeddedEntity
    SurveyResponse surveyResponse();
}
