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
import java.util.Collection;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;

@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface LeaseTermParticipant<LP extends LeaseParticipant<?>> extends IEntity {

    @I18n
    @XmlType(name = "TenantRole")
    public static enum Role implements Serializable {

        @Translate("Tenant")
        Applicant,

        @Translate("Co-Tenant")
        CoApplicant,

        Dependent,

        Guarantor;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static Collection<Role> resposible() {
            return EnumSet.of(Applicant, CoApplicant);
        }

        public static Collection<Role> tenantRelated() {
            return EnumSet.of(Applicant, CoApplicant, Dependent);
        }

        public static Collection<Role> portalAccess() {
            return EnumSet.of(Applicant, CoApplicant);
        }

        public static Collection<Role> roommates() {
            return EnumSet.of(CoApplicant, Dependent);
        }
    }

    interface LeaseParticipantHolderId extends ColumnId {
    }

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    @MemberColumn(notNull = true)
    @JoinColumn(LeaseParticipantHolderId.class)
    LP leaseParticipant();

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
    @MemberColumn(notNull = true)
    @Caption(name = "Lease Agreement")
    LeaseTermV leaseTermV();

    @OrderColumn
    IPrimitive<Integer> orderInLease();

    @NotNull
    @Indexed
    @Detached
    OnlineApplication application();

    /**
     * Recorded at the time of application approval
     */
    @Detached
    CustomerCreditCheck creditCheck();

    /**
     * Recorded at the time of application approval
     */
    @Detached
    @Versioned
    CustomerScreening screening();

    @Transient
    CustomerScreening effectiveScreening();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    AgreementSignatures agreementSignatures();

}
