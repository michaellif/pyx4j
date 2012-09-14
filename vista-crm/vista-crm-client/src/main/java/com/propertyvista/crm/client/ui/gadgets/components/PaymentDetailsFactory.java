/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase.CounterDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.PaymentCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentDetailsFactory implements CounterDetailsFactory {

    private final IBuildingFilterContainer buildingFilterContainer;

    private final String filter;

    private final PaymentCriteriaProvider criteriaProvider;

    private final PaymentsDetailsLister lister;

    public PaymentDetailsFactory(PaymentCriteriaProvider criteriaProvider, IBuildingFilterContainer buildingFilterContainer, String filter) {
        this.buildingFilterContainer = buildingFilterContainer;
        this.filter = filter;
        this.criteriaProvider = criteriaProvider;
        this.lister = new PaymentsDetailsLister();
    }

    @Override
    public Widget createDetailsWidget() {
        criteriaProvider.makePaymentCriteria(new DefaultAsyncCallback<EntityListCriteria<PaymentRecordDTO>>() {

            @Override
            public void onSuccess(EntityListCriteria<PaymentRecordDTO> result) {
                ListerDataSource<PaymentRecordDTO> dataSource = new ListerDataSource<PaymentRecordDTO>(PaymentRecordDTO.class, GWT
                        .<PaymentCrudService> create(PaymentCrudService.class));

                List<Criterion> criteria = result.getFilters();
                if (criteria != null) {
                    dataSource.setPreDefinedFilters(criteria);
                }
                lister.setDataSource(dataSource);
                lister.obtain(0);
            }

        }, new Vector<Building>(buildingFilterContainer.getSelectedBuildingsStubs()), filter);
        return lister;
    }

}
