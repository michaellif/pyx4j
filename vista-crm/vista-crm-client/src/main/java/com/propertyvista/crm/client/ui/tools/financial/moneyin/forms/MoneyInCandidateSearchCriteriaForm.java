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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.crm.client.ui.tools.common.selectors.BuildingSelector;
import com.propertyvista.crm.client.ui.tools.common.selectors.CSuperSelector;
import com.propertyvista.crm.client.ui.tools.common.selectors.PortfolioSelector;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class MoneyInCandidateSearchCriteriaForm extends CEntityForm<MoneyInCandidateSearchCriteriaModel> {

    private static class SearchCriteriaFormDecoratorBuilder extends FieldDecoratorBuilder {

        public SearchCriteriaFormDecoratorBuilder() {
            super();
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
    protected IsWidget createContent() {
        FlowPanel panel = new FlowPanel();

        panel.add(inject(proto().portfolios(), createPortfolioSelector()));
        panel.add(inject(proto().buildings(), createBuildingSelector()));
        panel.add(inject(proto().unit(), new SearchCriteriaFormDecoratorBuilder().componentWidth("100px").build()));
        panel.add(inject(proto().lease(), new SearchCriteriaFormDecoratorBuilder().componentWidth("100px").build()));
        panel.add(inject(proto().tenant(), new SearchCriteriaFormDecoratorBuilder().componentWidth("150px").build()));

        return panel;

    }

    public int getRequiredHeight() {
        return TOP_ROW_HEIGHT + Math.max(portfolioSelector.getElement().getScrollHeight(), buildingSelector.getElement().getScrollHeight());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        setTextBoxHeightFieldsHack();
    }

    protected void onSuperSelectorResized() {

    }

    private CSuperSelector<PortfolioForSelectionDTO> createPortfolioSelector() {
        return new CSuperSelector<PortfolioForSelectionDTO>(portfolioSelector = new PortfolioSelector() {//@formatter:off
            @Override protected void onItemAdded(PortfolioForSelectionDTO item) {
                super.onItemAdded(item);
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
                super.onItemAdded(item);
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

    private void setTextBoxHeightFieldsHack() {
        // we use this hack to set height of text boxes same as selector boxes, because I haven't found any other way to equalize the height
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                try {
                    if (isEditable()) {
                        setTextBoxHeightFieldHack(get(proto().unit()).getDecorator().asWidget().getElement().getElementsByTagName("input").getItem(0));
                        setTextBoxHeightFieldHack(get(proto().lease()).getDecorator().asWidget().getElement().getElementsByTagName("input").getItem(0));
                        setTextBoxHeightFieldHack(get(proto().tenant()).getDecorator().asWidget().getElement().getElementsByTagName("input").getItem(0));
                    }
                } catch (Throwable e) {
                    // this is a hack, hence it should suffer silently 
                }
            }
        });

    }

    private void setTextBoxHeightFieldHack(Element el) {
        el.getStyle().setHeight(2.5, Unit.EM);
    }

}
