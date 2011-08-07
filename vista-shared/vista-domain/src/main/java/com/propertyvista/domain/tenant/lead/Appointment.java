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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.contact.Phone;

public interface Appointment extends IEntity {

    @Translatable
    public enum Status {

        planned,

        noShow,

        complete;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Format("MM/dd/yyyy")
    @MemberColumn(name = "appointmentDate")
    IPrimitive<LogicalDate> date();

    @MemberColumn(name = "appointmentTime")
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> time();

    IPrimitive<String> address();

    IPrimitive<String> agent();

    @Editor(type = EditorType.phone)
    @EmbeddedEntity
    Phone phone();

    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    IPrimitive<Status> status();
}
