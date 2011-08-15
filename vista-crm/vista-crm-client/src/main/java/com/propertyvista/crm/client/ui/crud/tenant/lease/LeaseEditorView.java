/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.pyx4j.site.client.ui.crud.IEditorView;
import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseEditorView extends IEditorView<LeaseDTO> {

    interface Presenter extends IEditorView.Presenter {

        IListerView.Presenter getBuildingPresenter();

        IListerView.Presenter getUnitPresenter();

        IListerView.Presenter getTenantPresenter();

        void setSelectedBuilding(Building selected);

        void setSelectedService(ServiceItem serviceItem);

        void removeTeant(TenantInLease tenant);
    }

    IListerView<Building> getBuildingListerView();

    IListerView<AptUnit> getUnitListerView();

    IListerView<Tenant> getTenantListerView();

}
