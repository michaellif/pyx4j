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
import java.util.Date;

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
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;

public interface MaintenanceRequest extends IEntity {

    @Owner
    @Detached
    @NotNull
    @MemberColumn(notNull = true)
    @JoinColumn
    Building building();

    AptUnit unit();

    AbstractPmcUser originator();

    // --------------------------------------

    Tenant reporter();

    IPrimitive<String> reporterName();

    @Editor(type = EditorType.phone)
    IPrimitive<String> reporterPhone();

    @Editor(type = EditorType.email)
    IPrimitive<String> reporterEmail();

    // --------------------------------------

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
    IPrimitive<Date> submitted();

    @Caption(name = "Last Updated")
    @Timestamp(Update.Updated)
    IPrimitive<Date> updated();

    // --------------------------------------

    @Length(2048)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    IPrimitive<String> summary();

    // --------------------------------------

    IPrimitive<Boolean> permissionToEnter();

    @Length(2048)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> petInstructions();

    // --------------------------------------

    @I18n
    public enum DayTime {

        @Translate("9am-12am")
        Morning,

        @Translate("12am-3pm")
        Afternoon,

        @Translate("3pm-6pm")
        Evening;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<LogicalDate> preferredDate1();

    @Format("h:mm a")
    IPrimitive<DayTime> preferredTime1();

    IPrimitive<LogicalDate> preferredDate2();

    @Format("h:mm a")
    IPrimitive<DayTime> preferredTime2();

    // --------------------------------------

    IPrimitive<LogicalDate> scheduledDate();

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    IPrimitive<Time> scheduledTime();

    // --------------------------------------

    @Length(2048)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> resolution();

    // --------------------------------------

    @EmbeddedEntity
    SurveyResponse surveyResponse();
}
