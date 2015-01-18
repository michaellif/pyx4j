/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.selector.IOptionsGrabber;
import com.pyx4j.widgets.client.selector.SingleWordSuggestOptionsGrabber;

import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.dto.communication.CommunicationEndpointDTO;

public class CommunicationEndpointOptionsGrabber extends SingleWordSuggestOptionsGrabber<CommunicationEndpointDTO> {

    public CommunicationEndpointOptionsGrabber() {
        super(GWT.<SelectCommunicationEndpointListService> create(SelectCommunicationEndpointListService.class));
    }

    @Override
    public void grabOptions(final IOptionsGrabber.Request request, final IOptionsGrabber.Callback<CommunicationEndpointDTO> callback) {

        AsyncCallback<Vector<CommunicationEndpointDTO>> callbackOptionsGrabber = new DefaultAsyncCallback<Vector<CommunicationEndpointDTO>>() {
            @Override
            public void onSuccess(Vector<CommunicationEndpointDTO> result) {
                filter(result, request.getQuery().toLowerCase());
                callback.onOptionsReady(request, new Response<CommunicationEndpointDTO>(filtered));
            }

        };
        EntityListCriteria<CommunicationEndpointDTO> criteria = EntityListCriteria.create(CommunicationEndpointDTO.class);
        criteria.setPageSize(request.getLimit());
        criteria.eq(criteria.proto().name(), request.getQuery().toLowerCase());
        ((SelectCommunicationEndpointListService) service).getEndpointForSelection(callbackOptionsGrabber, criteria);

    }

    @Override
    protected int evaluate(CommunicationEndpointDTO item, String suggestion) {
        if (item.name().getValue().toLowerCase().contains(suggestion)) {
            return 1;
        } else {
            return 0;
        }
    }
}
