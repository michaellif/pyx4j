/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsState;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.security.TenantUserHolder;

@DiscriminatorValue("Tenant")
public interface Tenant extends IEntity, PersonScreeningHolder, TenantUserHolder {

    @I18n
    @XmlType(name = "TenantType")
    public enum Type {

        person,

        company;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ReadOnly
    IPrimitive<String> tenantID();

    @NotNull
    @MemberColumn(name = "tenantType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    @EmbeddedEntity
    Company company();

    @Owned
// TODO : commented because of strange behavior of with @Owned - entities duplicated on loading/saving...  
//    @Detached
    @Length(3)
    IList<EmergencyContact> emergencyContacts();

    @Timestamp
    IPrimitive<Date> updated();

    /**
     * This used to enforce Data access
     */
    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = TenantInLease.class, cascade = false)
    ISet<TenantInLease> _tenantInLease();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<MaintenanceRequest> _MaintenanceRequests();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<ArrearsState> _ArrearsState();

}
