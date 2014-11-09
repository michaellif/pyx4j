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
package com.propertyvista.crm.client.ui.crud.customer.lead.appointment;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentListerViewImpl extends CrmListerViewImplBase<Appointment> implements AppointmentListerView {

    public AppointmentListerViewImpl() {
        setLister(new AppointmentLister());
    }

    @Override
    public void setAddNewVisible(boolean visible) {
        getLister().getAddButton().setVisible(visible);
    }
}
