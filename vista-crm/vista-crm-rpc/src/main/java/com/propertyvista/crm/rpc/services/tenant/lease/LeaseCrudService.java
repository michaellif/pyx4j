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
package com.propertyvista.crm.rpc.services.tenant.lease;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseCrudService extends AbstractVersionedCrudService<LeaseDTO> {

    void setSelectededUnit(AsyncCallback<AptUnit> callback, Key unitId);

    void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item);

    // TODO Move to new Service  LeaseOperationService
    void startApplication(AsyncCallback<VoidSerializable> callback, Key entityId);

    void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO);

    void notice(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut);

    void cancelNotice(AsyncCallback<VoidSerializable> callback, Key entityId);

    void evict(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut);

    void cancelEvict(AsyncCallback<VoidSerializable> callback, Key entityId);

    void sendMail(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<Tenant> tenants, EmailTemplateType emailType);
}
