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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

@I18nComment("Potential customer")
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

        declined,

        rented;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 0)
    @EmbeddedEntity
    Person person();

    @Caption(name = "Move-in date")
    IPrimitive<LogicalDate> moveInDate();

    @ToString(index = 1)
    @Caption(name = "Lease Length")
    IPrimitive<LeaseTerm> leaseTerm();

    @Transient
    // calculated from selected unit below!
    Building building();

    @NotNull
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

    Employee agent();

    IPrimitive<Status> status();

    Lease lease();

    @ReadOnly
    IPrimitive<LogicalDate> createDate();

// double reference - currently use just back reference from Appointment itself.
//  IList<Appointment> appointments();
}
