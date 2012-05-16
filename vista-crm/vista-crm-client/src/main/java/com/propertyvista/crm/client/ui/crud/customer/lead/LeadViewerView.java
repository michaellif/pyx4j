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
package com.propertyvista.crm.client.ui.crud.customer.lead;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public interface LeadViewerView extends IViewerView<Lead> {

    interface Presenter extends IViewerView.Presenter {

        void getInterestedUnits(AsyncCallback<List<AptUnit>> callback);

        void convertToLease(Key unitId);

        void close();
    }

    IListerView<Appointment> getAppointmentsListerView();

    public void onLeaseConvertionSuccess();

    // may return TRUE in case of processed event and no need to re-throw the exception further.
    // FALSE - re-throws the exception (new UnrecoverableClientError(caught)).
    boolean onConvertionFail(Throwable caught);
}
