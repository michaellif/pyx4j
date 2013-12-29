/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

// TODO refactor and merge duplication with PortfolioSuggestionsProvider (don't forget about possible pagination)
class BuildingSuggestionsProvider extends SuperSuggestiveSelector.SuggestionsProvider<BuildingForSelectionDTO> {

    private static final class RankedMatch implements Comparable<RankedMatch> {

        private final BuildingForSelectionDTO building;

        private final int priority;

        private final Comparator<BuildingForSelectionDTO> tieBreakingComparator;

        public RankedMatch(int priority, BuildingForSelectionDTO building, Comparator<BuildingForSelectionDTO> tieBreakingComparator) {
            this.building = building;
            this.tieBreakingComparator = tieBreakingComparator;
            this.priority = priority;
        }

        @Override
        public int compareTo(RankedMatch o) {
            if (this.priority == o.priority) {
                return tieBreakingComparator.compare(this.building, o.building);
            } else if (this.priority > o.priority) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private final SelectBuildingListService service;

    private final Comparator<BuildingForSelectionDTO> buildingComparator;

    private EntityQueryCriteria<Building> prevCriteria;

    private Vector<BuildingForSelectionDTO> cachedBuildings;

    private List<BuildingForSelectionDTO> filteredBuildings;

    public BuildingSuggestionsProvider() {
        service = createCachingProxyForSelectBuildingListService();
        cachedBuildings = new Vector<BuildingForSelectionDTO>();
        filteredBuildings = new LinkedList<BuildingForSelectionDTO>();

        buildingComparator = new Comparator<BuildingForSelectionDTO>() {
            @Override
            public int compare(BuildingForSelectionDTO o1, BuildingForSelectionDTO o2) {
                return o1.propertyCode().getValue().compareTo(o2.propertyCode().getValue());
            }
        };

    }

    @Override
    public void onSuggestionCriteriaChange(final String newSuggestion) {
        AsyncCallback<Vector<BuildingForSelectionDTO>> callback = new DefaultAsyncCallback<Vector<BuildingForSelectionDTO>>() {
            @Override
            public void onSuccess(Vector<BuildingForSelectionDTO> buildings) {
                filteredBuildings = filter(buildings, newSuggestion);
                updateRowCount(filteredBuildings.size(), true);
                updateRowData(0, filteredBuildings);
            }
        };
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        service.getBuildingsForSelection(callback, criteria);
    }

    @Override
    protected void onRangeChanged(HasData<BuildingForSelectionDTO> display) {
        updateRowData(display, 0, filteredBuildings);
    }

    /**
     * This is used to determine how well the suggestion matches an entity.
     * 
     * @return match score: <code>0 or less</code> means doesn't match, then the higher the number the better.
     */
    // TODO find a better name
    protected int evaluate(BuildingForSelectionDTO buiding, String suggestion) {//@formatter:off
        if (buiding.propertyCode().getValue().toLowerCase().contains(suggestion)) {
            return 2;
        } else if (buiding.name().getValue().toLowerCase().contains(suggestion)) {
            return 2;
        } else if (buiding.address().getValue().toLowerCase().contains(suggestion)) {
            return 1;
        } else {
            return 0;
        }
    }//@formatter:on

    private List<BuildingForSelectionDTO> filter(Vector<BuildingForSelectionDTO> buildings, String newSuggestion) {
        List<BuildingForSelectionDTO> filtered = new LinkedList<BuildingForSelectionDTO>();
        if (newSuggestion == null || "".equals(newSuggestion)) {
            filtered.addAll(buildings);
        } else {
            PriorityQueue<RankedMatch> queue = new PriorityQueue<BuildingSuggestionsProvider.RankedMatch>();
            for (BuildingForSelectionDTO b : buildings) {
                int rank = evaluate(b, newSuggestion);
                if (rank > 0) {
                    queue.add(new RankedMatch(rank, b, buildingComparator));
                }
            }
            while (!queue.isEmpty()) {
                filtered.add(queue.poll().building);
            }
        }
        return filtered;
    }

    private SelectBuildingListService createCachingProxyForSelectBuildingListService() {
        return new SelectBuildingListService() {//@formatter:off          
            private final SelectBuildingListService delegatedService =GWT.<SelectBuildingListService> create(SelectBuildingListService.class);                    

            @Override public void getBuildingsForSelection(final AsyncCallback<Vector<BuildingForSelectionDTO>> callback, final EntityQueryCriteria<Building> criteria) {
                if (prevCriteria == null || !prevCriteria.equals(criteria)) {
                    delegatedService.getBuildingsForSelection(new AsyncCallback<Vector<BuildingForSelectionDTO>>() {
                        @Override public void onSuccess(Vector<BuildingForSelectionDTO> result) {
                            prevCriteria = criteria;
                            cachedBuildings = result;
                            callback.onSuccess(result);
                        }
                        
                        @Override public void onFailure(Throwable caught) { callback.onFailure(caught); }
                    }, criteria);
                } else {
                    callback.onSuccess(cachedBuildings);
                }
            };
            
            // we're not going to need these two methods
            @Override public void list(AsyncCallback<EntitySearchResult<Building>> callback, EntityListCriteria<Building> criteria) {}            
            @Override public void delete(AsyncCallback<Boolean> callback, Key entityId) {}
        };//@formatter:off
    }
    
    

}
