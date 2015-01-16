/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-21
 * @author ArtyomB
 */
package com.propertyvista.domain.legal;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.Lease;

@DiscriminatorValue("LegalStatus")
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
@ToStringFormat("{0}{1,choice,null#|!null#, since {1}}{2,choice,null#|!null#, expiry {2}}")
public interface LegalStatus extends IEntity {

    @I18n(context = "Legal Status")
    @XmlType(name = "LegalStatus")
    public enum Status {

        None,

        @Translate("N4CP")
        N4CP,

        @Translate("N4CS")
        N4CS,

        @Translate("N4")
        N4,

        @Translate("L1")
        L1,

        HearingDate,

        Order,

        Sheriff,

        SetAside,

        RequestToReviewOrder,

        StayOrder;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    @Detached
    @MemberColumn(notNull = true)
    @ReadOnly(allowOverrideNull = false)
    Lease lease();

    @NotNull
    @ToString(index = 0)
    IPrimitive<Status> status();

    /** User defined wording that describe the status */
    IPrimitive<String> details();

    /** Notes that describe how this status was generated, this is set by system and not by user */
    IPrimitive<String> notes();

    /** This is the timestamp of when this status has been set */
    @ToString(index = 1)
    IPrimitive<Date> setOn();

    /** This is planned or actual expiry date */
    @ToString(index = 2)
    IPrimitive<Date> expiry();

    @Detached
    CrmUser setBy();

}
