/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.forms;

import java.util.Vector;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CEntityForm;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector;
import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector.SuggestionsProvider;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

public class MoneyInCandidateSearchCriteriaForm extends CEntityForm<MoneyInCandidateDTO> {

    public MoneyInCandidateSearchCriteriaForm() {
        super(MoneyInCandidateDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();

        SuggestionsProvider<Building> buildingProvider = new SuperSuggestiveSelector.SuggestionsProvider<Building>() {
            SelectBuildingListService service = GWT.create(SelectBuildingListService.class);

            @Override
            public void onSuggestionCriteriaChange(String newSuggestion) {
                if (newSuggestion != null) {
                    EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                    criteria.like(criteria.proto().propertyCode(), newSuggestion);
                    service.getAll(new AsyncCallback<Vector<Building>>() {

                        @Override
                        public void onSuccess(Vector<Building> result) {
                            updateRowData(0, result);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            // TODO Auto-generated method stub

                        }
                    }, criteria);
                }
            }

            @Override
            protected void onRangeChanged(HasData<Building> display) {
                // TODO Auto-generated method stub

            }
        };
        AbstractCell<Building> buildingCell = new AbstractCell<Building>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Building value, SafeHtmlBuilder sb) {
                if (value != null) {
                    sb.appendEscaped(value.getStringView());
                }
            }
        };

        SuperSuggestiveSelector<Building> buildingSelector = new SuperSuggestiveSelector<Building>(buildingCell, buildingProvider) {
            @Override
            protected Building parseItem(String stringRepresentation) {
                return null;
            }
        };
        buildingSelector.setWidth("300px");

        panel.add(buildingSelector);
        return panel;
    }

}
