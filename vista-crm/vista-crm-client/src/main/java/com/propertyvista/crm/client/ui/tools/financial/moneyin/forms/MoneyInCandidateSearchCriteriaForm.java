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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.tools.common.selectors.BuildingSelector;
import com.propertyvista.crm.client.ui.tools.common.selectors.CSuperSelector;
import com.propertyvista.crm.client.ui.tools.common.selectors.PortfolioSelector;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class MoneyInCandidateSearchCriteriaForm extends CEntityForm<MoneyInCandidateSearchCriteriaDTO> {

    private static class SearchCriteriaFormDecoratorBuilder extends FormDecoratorBuilder {

        public SearchCriteriaFormDecoratorBuilder(CComponent<?> component) {
            super(component);
            labelPosition(LabelPosition.top);
        }

        @Override
        public Builder componentWidth(String componentWidth) {
            labelWidth(componentWidth);
            contentWidth(componentWidth);
            return super.componentWidth(componentWidth);
        }
    }

    public MoneyInCandidateSearchCriteriaForm() {
        super(MoneyInCandidateSearchCriteriaDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();

        panel.add(new SearchCriteriaFormDecoratorBuilder(inject(proto().portfolios(), createPortfolioSelector())).componentWidth("300px").build());
        panel.add(new SearchCriteriaFormDecoratorBuilder(inject(proto().buildings(), createBuildingSelector())).componentWidth("300px").build());
        panel.add(new SearchCriteriaFormDecoratorBuilder(inject(proto().unit())).componentWidth("100px").build());
        panel.add(new SearchCriteriaFormDecoratorBuilder(inject(proto().lease())).componentWidth("100px").build());
        panel.add(new SearchCriteriaFormDecoratorBuilder(inject(proto().tenant())).componentWidth("150px").build());

        return panel;
    }

    private CSuperSelector<PortfolioForSelectionDTO> createPortfolioSelector() {
        return new CSuperSelector<PortfolioForSelectionDTO>(new PortfolioSelector() {//@formatter:off
            @Override protected void onItemAdded(PortfolioForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaDTO searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.portfolios().add(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);                
            }
            @Override
            protected void onItemRemoved(PortfolioForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaDTO searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.portfolios().remove(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);            
            }
        });//@formatter:on
    }

    private CSuperSelector<BuildingForSelectionDTO> createBuildingSelector() {
        return new CSuperSelector<BuildingForSelectionDTO>(new BuildingSelector() {//@formatter:off
            @Override protected void onItemAdded(BuildingForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaDTO searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.buildings().add(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);
            }            
            @Override protected void onItemRemoved(BuildingForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaDTO searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue();
                searchCriteria.buildings().remove(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);
            }            
        });//@formatter:on
    }

}
