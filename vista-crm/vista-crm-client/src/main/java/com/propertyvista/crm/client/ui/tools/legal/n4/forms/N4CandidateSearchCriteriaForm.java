/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4.forms;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.crm.client.ui.tools.common.selectors.BuildingSelector;
import com.propertyvista.crm.client.ui.tools.common.selectors.PortfolioSelector;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class N4CandidateSearchCriteriaForm extends CForm<N4CandidateSearchCriteriaDTO> {

    private static class SearchCriteriaFormDecoratorBuilder extends FieldDecoratorBuilder {

        public SearchCriteriaFormDecoratorBuilder() {
            super();
            labelPosition(LabelPosition.top);
        }

        @Override
        public FieldDecoratorBuilder componentWidth(String componentWidth) {
            labelWidth(componentWidth);
            return super.componentWidth(componentWidth);
        }
    }

    private CLabel<String> policyErrorsLabel;

    private FlowPanel searchCriteriaPanel;

    private PortfolioSelector portfolioSelector;

    private BuildingSelector buildingSelector;

    public N4CandidateSearchCriteriaForm() {
        super(N4CandidateSearchCriteriaDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.getElement().getStyle().setOverflow(Overflow.AUTO);
        panel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        panel.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);

        policyErrorsLabel = new CLabel<String>();
        policyErrorsLabel.asWidget().getElement().getStyle().setTextAlign(TextAlign.CENTER);
        policyErrorsLabel.asWidget().getElement().getStyle().setPaddingTop(2, Unit.EM);
        policyErrorsLabel.asWidget().getElement().getStyle().setPaddingBottom(2, Unit.EM);
        policyErrorsLabel.asWidget().getElement().getStyle().setProperty("marginLeft", "auto");
        policyErrorsLabel.asWidget().getElement().getStyle().setProperty("marginRight", "auto");
        panel.setWidget(0, 0, 2, inject(proto().n4PolicyErrors(), policyErrorsLabel));

        int row = 0;

        searchCriteriaPanel = new FlowPanel();

        searchCriteriaPanel.add(createPortfolioSelector());
        searchCriteriaPanel.add(createBuildingSelector());
        searchCriteriaPanel.add(inject(proto().minAmountOwed(), new SearchCriteriaFormDecoratorBuilder().componentWidth("200px").build()));

        panel.setWidget(1, 0, 2, searchCriteriaPanel);

        return panel;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        addComponentValidator(new AbstractComponentValidator<N4CandidateSearchCriteriaDTO>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && !CommonsStringUtils.isEmpty(getComponent().getValue().n4PolicyErrors().getValue())) {
                    return new BasicValidationError(getComponent(), getComponent().getValue().n4PolicyErrors().getValue());
                }
                return null;
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateComponentsVisibility();
    }

    private void updateComponentsVisibility() {
        policyErrorsLabel.setVisible(!CommonsStringUtils.isEmpty(getValue().n4PolicyErrors().getValue()));
        searchCriteriaPanel.setVisible(CommonsStringUtils.isEmpty(getValue().n4PolicyErrors().getValue()));
    }

    private IsWidget createPortfolioSelector() {
        return portfolioSelector = new PortfolioSelector() {//@formatter:off
            @Override protected void onItemAdded(PortfolioForSelectionDTO item) {
                super.onItemAdded(item);
                N4CandidateSearchCriteriaDTO searchCriteria = N4CandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.portfolios().add(item);
                N4CandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);                
            }
            @Override
            protected void onItemRemoved(PortfolioForSelectionDTO item) {
                N4CandidateSearchCriteriaDTO searchCriteria = N4CandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.portfolios().remove(item);
                N4CandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);            
            }
            
        };//@formatter:on
    }

    private IsWidget createBuildingSelector() {
        return buildingSelector = new BuildingSelector() {//@formatter:off
            @Override protected void onItemAdded(BuildingForSelectionDTO item) {
                super.onItemAdded(item);
                N4CandidateSearchCriteriaDTO searchCriteria = N4CandidateSearchCriteriaForm.this.getValue(); 
                searchCriteria.buildings().add(item);
                N4CandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);
            }            
            @Override protected void onItemRemoved(BuildingForSelectionDTO item) {
                N4CandidateSearchCriteriaDTO  searchCriteria = N4CandidateSearchCriteriaForm.this.getValue();
                searchCriteria.buildings().remove(item);
                N4CandidateSearchCriteriaForm.this.setValue(searchCriteria, true, false);
            }
        };//@formatter:on
    }

}
