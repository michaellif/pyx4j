/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.tenant.lead;

import java.io.Serializable;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public interface LeadCrudService extends AbstractCrudService<Lead> {

    void setSelectedFloorplan(AsyncCallback<Floorplan> callback, Key floorplanId);

    void getInterestedUnits(AsyncCallback<Vector<AptUnit>> callback, Key leadId);

    void convertToLease(AsyncCallback<Lease> callback, Key leadId, Key unitId);

    void close(AsyncCallback<Serializable> callback, Key leadId);
}
