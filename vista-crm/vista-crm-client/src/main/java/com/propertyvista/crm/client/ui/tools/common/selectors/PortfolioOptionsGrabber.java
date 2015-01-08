/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.selector.IOptionsGrabber;
import com.pyx4j.widgets.client.selector.SingleWordSuggestOptionsGrabber;

import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioOptionsGrabber extends SingleWordSuggestOptionsGrabber<PortfolioForSelectionDTO> {

    public PortfolioOptionsGrabber() {
        super(GWT.<SelectPortfolioListService> create(SelectPortfolioListService.class));
    }

    @Override
    public void grabOptions(final IOptionsGrabber.Request request, final IOptionsGrabber.Callback<PortfolioForSelectionDTO> callback) {

        AsyncCallback<Vector<PortfolioForSelectionDTO>> callbackOptionsGrabber = new DefaultAsyncCallback<Vector<PortfolioForSelectionDTO>>() {
            @Override
            public void onSuccess(Vector<PortfolioForSelectionDTO> result) {
                filter(result, request.getQuery().toLowerCase());
                callback.onOptionsReady(request, new Response<PortfolioForSelectionDTO>(filtered));
            }

        };

        EntityListCriteria<Portfolio> criteria = EntityListCriteria.create(Portfolio.class);
        criteria.setPageSize(request.getLimit());
        ((SelectPortfolioListService) service).getPortfoliosForSelection(callbackOptionsGrabber, criteria);

    }

    @Override
    protected int evaluate(PortfolioForSelectionDTO item, String suggestion) {
        if (item.name().getValue().toLowerCase().contains(suggestion)) {
            return 1;
        } else {
            return 0;
        }
    }
}
