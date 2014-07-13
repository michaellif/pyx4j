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
package com.propertyvista.crm.client.activity.crud.financial.paps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.financial.paps.PapListerView;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.domain.payment.AutopayAgreement;

public class PapListerActivity extends AbstractListerActivity<AutoPayHistoryDTO> {

    public PapListerActivity(Place place) {
        super(AutoPayHistoryDTO.class, place, CrmSite.getViewFactory().getView(PapListerView.class), GWT.<AutoPayHistoryCrudService> create(AutoPayHistoryCrudService.class));
    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<AutoPayHistoryDTO> entityClass, EntityFiltersBuilder<AutoPayHistoryDTO> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        AutopayAgreement argProto = EntityFactory.getEntityPrototype(AutopayAgreement.class);

        String val;
        if ((val = place.getFirstArg(argProto.tenant().participantId().getPath().toString())) != null) {
            filters.eq(filters.proto().tenant().participantId(), val);
        }
        if ((val = place.getFirstArg(argProto.tenant().lease().leaseId().getPath().toString())) != null) {
            filters.eq(filters.proto().tenant().lease().leaseId(), val);
        }
        if ((val = place.getFirstArg(argProto.isDeleted().getPath().toString())) != null) {
            filters.eq(filters.proto().isDeleted(), Boolean.valueOf(val));
        }
    }
}
