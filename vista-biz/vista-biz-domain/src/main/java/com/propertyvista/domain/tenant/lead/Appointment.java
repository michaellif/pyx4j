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

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;

public interface Appointment extends IEntity {

    @I18n
    public enum Status {

        planned,

        closed;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    @I18nComment("Potential customer")
    Lead lead();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "appointmentDate")
    IPrimitive<LogicalDate> date();

    @NotNull
    @ToString(index = 1)
    @MemberColumn(name = "appointmentTime")
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> time();

    @ToString(index = 2)
    IPrimitive<String> address();

    Employee agent();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @NotNull
    @ToString(index = 3)
    IPrimitive<Status> status();

    IPrimitive<String> closeReason();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> notes();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Showing> showings();
}
