/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;

@Inheritance
@AbstractEntity
public interface LeaseParticipant extends IEntity {

    @I18n
    @XmlType(name = "TenantRole")
    public static enum Role implements Serializable {

        Applicant,

        @Translate("Co-Applicant")
        CoApplicant,

        Dependent,

        Guarantor;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    Customer customer();

    @NotNull
    @ToString(index = 1)
    @MemberColumn(name = "participantRole")
    IPrimitive<Role> role();

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    @Caption(name = "Lease")
    Lease.LeaseV leaseV();

    @OrderColumn
    IPrimitive<Integer> orderInLease();

    @NotNull
    @Indexed
    @Detached
    OnlineApplication application();

    @Detached
    PersonScreening screening();

}
