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
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.financial.LeaseFinancialTerms;
import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;

public interface Lease extends IEntity {

    @Translatable
    public enum Status {

        ApplicationCreated,

        ApplicationSubmited,

        ApplicationDeclined,

        @Deprecated
        approved // ??

        // TODO
        ;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    IPrimitive<Status> status();

    // double reference
    @Detached
    IList<TenantInLease> tenants();

// TODO check if we need Owned flag:    
    @Owned
    IList<Vehicle> vehicles();

    IList<Pet> pets();

    // --------- OTHER Old Suff below needs verification, DO NOT USE it ---

    @ToString(index = 0)
    IPrimitive<String> leaseID();

    AptUnit unit();

    // Dates:
    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    IPrimitive<LogicalDate> expectedMoveIn();

    IPrimitive<LogicalDate> expectedMoveOut();

    IPrimitive<LogicalDate> actualMoveIn();

    IPrimitive<LogicalDate> actualMoveOut();

    IPrimitive<LogicalDate> signDate();

    // Financial:
    IPrimitive<String> accountNumber();

    IPrimitive<Double> currentRent();

    IPrimitive<String> paymentAccepted();

    IList<ChargeLine> charges();

    IPrimitive<String> specialStatus();

    // TODO : there are utilities in the Unit already... is it the same? 
    IList<Utility> utilities();

    IList<Document> documents();

    IList<LeaseEvent> events();

    LeaseFinancialTerms leaseAgreement();

}
