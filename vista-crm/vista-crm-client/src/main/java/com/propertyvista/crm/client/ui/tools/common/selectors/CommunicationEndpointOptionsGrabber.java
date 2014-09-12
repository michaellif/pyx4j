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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.selector.SuggestiveSelector;
import com.pyx4j.widgets.client.suggest.IOptionsGrabber;

import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointOptionsGrabber implements IOptionsGrabber<CommunicationEndpointDTO> {

    private final SelectCommunicationEndpointListService service;

    private List<CommunicationEndpointDTO> filtered;

    public CommunicationEndpointOptionsGrabber() {
        service = //createCachingProxy(
        GWT.<SelectCommunicationEndpointListService> create(SelectCommunicationEndpointListService.class);
        filtered = new LinkedList<CommunicationEndpointDTO>();
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
        criteria.setPageSize(SuggestiveSelector.SUGGESTIONS_PER_PAGE);
        criteria.eq(criteria.proto().name(), request.getQuery().toLowerCase());
        service.getEndpointForSelection(callbackOptionsGrabber, criteria);

    }

    protected int evaluate(CommunicationEndpointDTO item, String suggestion) {
        if (item.name().getValue().toLowerCase().contains(suggestion)) {
            return 1;
        } else {
            return 0;
        }
    }

    private void filter(Vector<CommunicationEndpointDTO> result, String suggestion) {
        filtered = new LinkedList<CommunicationEndpointDTO>();
        if ("".equals(suggestion)) {
            filtered.addAll(result);
        } else {
            for (CommunicationEndpointDTO item : result) {
                if (evaluate(item, suggestion) > 0) {
                    filtered.add(item);
                }
            }
        }
    }
}
