/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.selector.SuggestiveSelector;

import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointSuggestionsProvider extends SuggestiveSelector.SuggestionsProvider<CommunicationEndpointDTO> {

    private final SelectCommunicationEndpointListService service;

    private EntityListCriteria<CommunicationEndpointDTO> prevCriteria;

    private Vector<CommunicationEndpointDTO> cached;

    private List<CommunicationEndpointDTO> filtered;

    public CommunicationEndpointSuggestionsProvider() {
        service = //createCachingProxy(
        GWT.<SelectCommunicationEndpointListService> create(SelectCommunicationEndpointListService.class);
        filtered = new LinkedList<CommunicationEndpointDTO>();
        cached = new Vector<CommunicationEndpointDTO>();
    }

    @Override
    public void onSuggestionCriteriaChange(final String newSuggestion) {
        AsyncCallback<Vector<CommunicationEndpointDTO>> callback = new DefaultAsyncCallback<Vector<CommunicationEndpointDTO>>() {
            @Override
            public void onSuccess(Vector<CommunicationEndpointDTO> result) {
                filter(result, newSuggestion.toLowerCase());
            }

        };
        EntityListCriteria<CommunicationEndpointDTO> criteria = EntityListCriteria.create(CommunicationEndpointDTO.class);
        criteria.setPageSize(SuggestiveSelector.SUGGESTIONS_PER_PAGE);
        criteria.eq(criteria.proto().name(), newSuggestion);
        service.getEndpointForSelection(callback, criteria);
    }

    @Override
    protected void onRangeChanged(HasData<CommunicationEndpointDTO> display) {
        updateRowCount(filtered.size(), true);
        updateRowData(display, 0, filtered);
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
        updateRowCount(filtered.size(), true);
        updateRowData(0, filtered);
    }

    private SelectCommunicationEndpointListService createCachingProxy(final SelectCommunicationEndpointListService service) {
        return new SelectCommunicationEndpointListService() {//@formatter:off
            private final SelectCommunicationEndpointListService delegatedService = service;

            @Override public void getEndpointForSelection(final AsyncCallback<Vector<CommunicationEndpointDTO>> callback, final EntityListCriteria<CommunicationEndpointDTO> criteria) {
                if (prevCriteria == null || !prevCriteria.equals(criteria)) {
                    delegatedService.getEndpointForSelection(new AsyncCallback<Vector<CommunicationEndpointDTO>>() {
                        @Override public void onSuccess(Vector<CommunicationEndpointDTO> result) {
                            prevCriteria = criteria;
                            cached = result;
                            callback.onSuccess(result);
                        }

                        @Override public void onFailure(Throwable caught) { callback.onFailure(caught); }
                    }, criteria);
                } else {
                    callback.onSuccess(cached);
                }
            };

            // we're not going to need these two methods
            @Override public void list(AsyncCallback<EntitySearchResult<CommunicationEndpoint>> callback, EntityListCriteria<CommunicationEndpoint> criteria) {}
            @Override public void delete(AsyncCallback<Boolean> callback, Key entityId) {}
        };//@formatter:off
    }
}
