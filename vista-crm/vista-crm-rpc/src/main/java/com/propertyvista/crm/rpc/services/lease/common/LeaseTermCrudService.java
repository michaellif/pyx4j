/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.lease.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseTermDTO;

public interface LeaseTermCrudService extends AbstractVersionedCrudService<LeaseTermDTO> {

    @Transient
    public interface LeaseTermInitializationData extends InitializationData {

        IPrimitive<Boolean> isOffer();

        Lease lease();

        IPrimitive<LeaseTerm.Type> termType();

        IPrimitive<ARCode.Type> leaseType();

        IPrimitive<Lease.Status> leaseStatus();

        AptUnit unit();
    }

    void setSelectedUnit(AsyncCallback<LeaseTermDTO> callback, AptUnit unitId, LeaseTermDTO currentValue);

    void setSelectedService(AsyncCallback<LeaseTermDTO> callback, ProductItem serviceId, LeaseTermDTO currentValue);

    void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem productItemId, LeaseTermDTO currentValue);

    void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item, LeaseTermDTO currentValue);

    void acceptOffer(AsyncCallback<VoidSerializable> callback, Key entityId);
}
