/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.financial.ServiceAgreement;
import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;

public interface Lease extends IEntity {

    @Translatable
    public enum Status {

        Draft,

        ApplicationInProgress,

        PendingApproval,

        Declined,

        Approved,

        Completed,

        Broken,

        Transferred;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    IPrimitive<String> leaseID();

    IPrimitive<Status> status();

    @Caption(name = "Selected Unit")
    AptUnit unit();

//  @Detached
    IList<TenantInLease> tenants(); // double reference (TenantInLease has reference to Lease already!)

    IList<Vehicle> vehicles();

    IList<Pet> pets();

    // Dates:
    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    IPrimitive<LogicalDate> expectedMoveIn();

    IPrimitive<LogicalDate> expectedMoveOut();

    IPrimitive<LogicalDate> actualMoveIn();

    IPrimitive<LogicalDate> actualMoveOut();

    IPrimitive<LogicalDate> moveOutNotice();

    IPrimitive<LogicalDate> signDate();

    ServiceAgreement serviceAgreement();

    IList<Document> documents();

}
