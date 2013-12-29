/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lead;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.shared.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.tenant.lease.Lease;

@I18nComment(value = "Potential customer", target = I18nComment.I18nCommentTarget.This)
public interface Lead extends IEntity {

    @I18n
    public enum RefSource {
        Internet, Newspaper, Radio, Referral, TV, Import, LocatorServices, DirectMail, Other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum LeaseTerm {

        @Translate("6 months")
        months6,

        @Translate("12 months")
        months12,

        @Translate("18 months")
        months18,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum DayPart {
        Morning, Afternoon, Evening;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum Status {

        active,

        closed,

        rented; // corresponds to Lease.Status.Active 

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum ConvertToLeaseAppraisal {

        @Translate("OK")
        Positive,

        @Translate("There are no active appointments.")
        NoAppointments,

        @Translate("There are no seen showings.")
        NoShowings,

        @Translate("Guest(s) has not shown interest in any Unit?")
        NoUnits;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owned
    IList<Guest> guests();

    @NotNull
    @ToString(index = 0)
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> leadId();

    @ToString(index = 1)
    @Caption(name = "Move-in date")
    IPrimitive<LogicalDate> moveInDate();

    @ToString(index = 2)
    @Caption(name = "Lease Length")
    IPrimitive<LeaseTerm> leaseTerm();

    @ToString(index = 3)
    IPrimitive<ARCode.Type> leaseType();

    Floorplan floorplan();

    @Caption(name = "Questions/Comments")
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> comments();

    @Caption(name = "How did you hear about us")
    IPrimitive<RefSource> refSource();

    // Preferred appointments:

    IPrimitive<LogicalDate> appointmentDate1();

    IPrimitive<DayPart> appointmentTime1();

    IPrimitive<LogicalDate> appointmentDate2();

    IPrimitive<DayPart> appointmentTime2();

    // === Internals:

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();

    @NotNull
    IPrimitive<Status> status();

    Employee agent();

    @Detached
    Lease lease();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Appointment> appointments();
}
