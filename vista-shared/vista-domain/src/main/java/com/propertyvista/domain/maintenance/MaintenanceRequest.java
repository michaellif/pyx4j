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

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.lease.Tenant;

public interface MaintenanceRequest extends IEntity {

    //TODO change Owner of MaintenanceRequest to property. Add originator - user that created MaintenanceRequest. 
    //Add optional Lease (or BuildingElement?) - can be selected in CRM or automatically assigned if MaintenanceRequest is created on portal
    @Owner
    @Detached
    @NotNull
    @MemberColumn(notNull = true)
    @JoinColumn
    Tenant leaseParticipant();

    @NotNull
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> requestId();

    @NotNull
    MaintenanceRequestCategory category();

    @NotNull
    MaintenanceRequestPriority priority();

    @NotNull
    MaintenanceRequestStatus status();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> submitted();

    @Caption(name = "Last Updated")
    @Timestamp(Update.Updated)
    IPrimitive<LogicalDate> updated();

    IPrimitive<LogicalDate> scheduledDate();

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    IPrimitive<Time> scheduledTime();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    IPrimitive<String> summary();

    IPrimitive<Boolean> permissionToEnter();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> petInstructions();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> resolution();

    @EmbeddedEntity
    SurveyResponse surveyResponse();

}
