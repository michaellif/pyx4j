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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioSuggestionsProvider extends SuperSuggestiveSelector.SuggestionsProvider<PortfolioForSelectionDTO> {

    private final SelectPortfolioListService service;

    private EntityListCriteria<Portfolio> prevCriteria;

    private Vector<PortfolioForSelectionDTO> cached;

    private List<PortfolioForSelectionDTO> filtered;

    public PortfolioSuggestionsProvider() {
        service = createCachingProxy(GWT.<SelectPortfolioListService> create(SelectPortfolioListService.class));
        filtered = new LinkedList<PortfolioForSelectionDTO>();
        cached = new Vector<PortfolioForSelectionDTO>();
    }

    @Override
    public void onSuggestionCriteriaChange(final String newSuggestion) {
        AsyncCallback<Vector<PortfolioForSelectionDTO>> callback = new DefaultAsyncCallback<Vector<PortfolioForSelectionDTO>>() {
            @Override
            public void onSuccess(Vector<PortfolioForSelectionDTO> result) {
                filter(result, newSuggestion.toLowerCase());
            }

        };
        EntityListCriteria<Portfolio> criteria = EntityListCriteria.create(Portfolio.class);
        service.getPortfoliosForSelection(callback, criteria);
    }

    @Override
    protected void onRangeChanged(HasData<PortfolioForSelectionDTO> display) {
        updateRowData(display, 0, filtered);
    }

    protected int evaluate(PortfolioForSelectionDTO item, String suggestion) {
        if (item.name().getValue().toLowerCase().contains(suggestion)) {
            return 1;
        } else {
            return 0;
        }
    }

    private void filter(Vector<PortfolioForSelectionDTO> result, String suggestion) {
        filtered = new LinkedList<PortfolioForSelectionDTO>();
        if ("".equals(suggestion)) {
            filtered.addAll(result);
        } else {
            for (PortfolioForSelectionDTO item : result) {
                if (evaluate(item, suggestion) > 0) {
                    filtered.add(item);
                }
            }
        }
        updateRowCount(filtered.size(), true);
        updateRowData(0, filtered);
    }

    private SelectPortfolioListService createCachingProxy(final SelectPortfolioListService service) {
        return new SelectPortfolioListService() {//@formatter:off
            private final SelectPortfolioListService delegatedService = service;

            @Override public void getPortfoliosForSelection(final AsyncCallback<Vector<PortfolioForSelectionDTO>> callback, final EntityListCriteria<Portfolio> criteria) {
                if (prevCriteria == null || !prevCriteria.equals(criteria)) {
                    delegatedService.getPortfoliosForSelection(new AsyncCallback<Vector<PortfolioForSelectionDTO>>() {
                        @Override public void onSuccess(Vector<PortfolioForSelectionDTO> result) {
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
            @Override public void list(AsyncCallback<EntitySearchResult<Portfolio>> callback, EntityListCriteria<Portfolio> criteria) {}
            @Override public void delete(AsyncCallback<Boolean> callback, Key entityId) {}
        };//@formatter:off
    }
}
