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
package com.propertyvista.crm.rpc.services.customer.lead;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Appointment;

public interface ShowingCrudService extends AbstractCrudService<ShowingDTO> {

    @Transient
    public interface ShowingInitializationData extends InitializationData {

        Appointment appointment();
    }

    void updateValue(AsyncCallback<AptUnit> callback, Key unitId);

    void getActiveState(AsyncCallback<Boolean> callback, Appointment appointmentId);
}
