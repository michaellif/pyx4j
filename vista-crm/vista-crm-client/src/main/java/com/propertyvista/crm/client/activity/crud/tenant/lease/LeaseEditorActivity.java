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
package com.propertyvista.crm.client.activity.crud.tenant.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.FeatureCrudService;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectTenantCrudService;
import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.crm.rpc.services.ServiceItemCrudService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorActivity extends EditorActivityBase<LeaseDTO> implements LeaseEditorView.Presenter {

    private final IListerView.Presenter buildingsLister;

    private final IListerView.Presenter unitsLister;

    private final IListerView.Presenter tenantsLister;

    private final IListerView.Presenter serviceItemLister;

    private final IListerView.Presenter featureLister;

    private final IListerView.Presenter concessionLister;

    @SuppressWarnings("unchecked")
    public LeaseEditorActivity(Place place) {
        super((LeaseEditorView) TenantViewFactory.instance(LeaseEditorView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class),
                LeaseDTO.class);

        buildingsLister = new ListerActivityBase<Building>(((LeaseEditorView) view).getBuildingListerView(),
                (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);

        unitsLister = new ListerActivityBase<AptUnit>(((LeaseEditorView) view).getUnitListerView(),
                (AbstractCrudService<AptUnit>) GWT.create(SelectUnitCrudService.class), AptUnit.class);

        tenantsLister = new ListerActivityBase<Tenant>(((LeaseEditorView) view).getTenantListerView(),
                (AbstractCrudService<Tenant>) GWT.create(SelectTenantCrudService.class), Tenant.class);

        featureLister = new ListerActivityBase<Feature>(((LeaseEditorView) view).getFeatureListerView(),
                (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class), Feature.class);

        serviceItemLister = new ListerActivityBase<ServiceItem>(((LeaseEditorView) view).getServiceItemListerView(),
                (AbstractCrudService<ServiceItem>) GWT.create(ServiceItemCrudService.class), ServiceItem.class);

        concessionLister = new ListerActivityBase<Concession>(((LeaseEditorView) view).getConcessionListerView(),
                (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class), Concession.class);

        withPlace(place);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return buildingsLister;
    }

    @Override
    public Presenter getUnitPresenter() {
        return unitsLister;
    }

    @Override
    public Presenter getTenantPresenter() {
        return tenantsLister;
    }

    @Override
    public Presenter getserviceItemPresenter() {
        return serviceItemLister;
    }

    @Override
    public Presenter getFeaturePresenter() {
        return featureLister;
    }

    @Override
    public Presenter getConcessionPresenter() {
        return concessionLister;
    }

    @Override
    public void onPopulateSuccess(LeaseDTO result) {

        buildingsLister.populate(0);
        tenantsLister.populate(0);

        populateUnitLister(result.selectedBuilding());
        fillserviceItems(result);

        super.onPopulateSuccess(result);
    }

    @Override
    public void setSelectedBuilding(Building selected) {
        LeaseDTO current = view.getValue();
        current.selectedBuilding().set(selected);
        current.serviceCatalog().set(selected.serviceCatalog());

        populateUnitLister(selected);
        fillserviceItems(current);

        view.populate(current);
    }

    public void populateUnitLister(Building selected) {
        if (!selected.isEmpty()) {
            unitsLister.setParentFiltering(selected.getPrimaryKey());
        }
        unitsLister.populate(0);
    }

    private void fillserviceItems(LeaseDTO currentValue) {
        for (Service service : currentValue.serviceCatalog().services()) {
            if (service.type().equals(currentValue.type())) {
                currentValue.selectedServiceItems().addAll(service.items());

                for (ServiceFeature feature : service.features()) {
                    currentValue.selectedFeatureItems().addAll(feature.feature().items());
                }

                for (ServiceConcession consession : service.concessions()) {
                    currentValue.selectedConcesions().add(consession.concession());
                }
            }
        }
    }
}
