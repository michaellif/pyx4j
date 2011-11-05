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
package com.propertyvista.crm.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseCrudService extends AbstractCrudService<LeaseDTO> {

    void setSelectededUnit(AsyncCallback<AptUnit> callback, Key unitId);

    void removeTenat(AsyncCallback<Boolean> callback, Key entityId);

    void calculateChargeItemAdjustments(AsyncCallback<Double> callback, ChargeItem item);

    void createMasterApplication(AsyncCallback<VoidSerializable> callback, Key entityId);
}
