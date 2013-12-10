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
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class MoneyInCandidateSearchCriteriaForm extends CEntityForm<MoneyInCandidateSearchCriteriaModel> {

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

    // TODO actually top row height depends on decorators labels but i'm to lazy right now and this seems to work fine
    private static final int TOP_ROW_HEIGHT = 50;

    private PortfolioSelector portfolioSelector;

    private BuildingSelector buildingSelector;

    public MoneyInCandidateSearchCriteriaForm() {
        super(MoneyInCandidateSearchCriteriaModel.class);
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

    public int getRequiredHeight() {
        return TOP_ROW_HEIGHT + Math.max(portfolioSelector.getElement().getScrollHeight(), buildingSelector.getElement().getScrollHeight());
    }

    protected void onSuperSelectorResized() {

    }

    private CSuperSelector<PortfolioForSelectionDTO> createPortfolioSelector() {
        return new CSuperSelector<PortfolioForSelectionDTO>(portfolioSelector = new PortfolioSelector() {//@formatter:off
            @Override protected void onItemAdded(PortfolioForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaModel searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.portfolios().add(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);                
            }
            @Override
            protected void onItemRemoved(PortfolioForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaModel searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.portfolios().remove(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);            
            }
            
            @Override protected void onRedraw() {
                super.onRedraw();
                MoneyInCandidateSearchCriteriaForm.this.onSuperSelectorResized();
            }
        });//@formatter:on
    }

    private CSuperSelector<BuildingForSelectionDTO> createBuildingSelector() {
        return new CSuperSelector<BuildingForSelectionDTO>(buildingSelector = new BuildingSelector() {//@formatter:off
            @Override protected void onItemAdded(BuildingForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaModel searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.buildings().add(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);
            }            
            @Override protected void onItemRemoved(BuildingForSelectionDTO item) {
                MoneyInCandidateSearchCriteriaModel searchCriteria = MoneyInCandidateSearchCriteriaForm.this.getValue();
                searchCriteria.buildings().remove(item);
                MoneyInCandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);
            }
            @Override protected void onRedraw() {
                super.onRedraw();
                MoneyInCandidateSearchCriteriaForm.this.onSuperSelectorResized();
            }
        });//@formatter:on
    }

}
