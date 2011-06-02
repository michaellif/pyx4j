/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-30
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface LeaseEvent extends IEntity {
    @Translatable
    public enum Type {

        application,

        leaseSigned,

        leaseFrom,

        leaseTo,

        expectedMoveIn,

        actualMoveIn,

        evictionNoticeGiven,

        moveOutNoticeGiven,

        expectedMoveOut,

        actualMoveOut,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    @MemberColumn(name = "eventType")
    IPrimitive<Type> type();

    @MemberColumn(name = "eventDate")
    IPrimitive<LogicalDate> date();

    IPrimitive<String> notes();
}
