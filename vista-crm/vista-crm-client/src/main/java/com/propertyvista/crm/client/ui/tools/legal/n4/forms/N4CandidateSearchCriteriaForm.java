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

import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorLabel;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.crm.client.ui.tools.common.selectors.BuildingSelector;
import com.propertyvista.crm.client.ui.tools.common.selectors.PortfolioSelector;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class N4CandidateSearchCriteriaForm extends CForm<N4CandidateSearchCriteriaDTO> {

    public static final I18n i18n = I18n.get(N4CandidateSearchCriteriaForm.class);

    private CLabel<String> policyErrorsLabel;

    public N4CandidateSearchCriteriaForm() {
        super(N4CandidateSearchCriteriaDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.getElement().getStyle().setOverflow(Overflow.AUTO);

        policyErrorsLabel = new CLabel<String>();
        policyErrorsLabel.asWidget().getElement().getStyle().setTextAlign(TextAlign.CENTER);
        policyErrorsLabel.asWidget().getElement().getStyle().setPaddingTop(1, Unit.EM);
        policyErrorsLabel.asWidget().getElement().getStyle().setPaddingBottom(1, Unit.EM);
        policyErrorsLabel.asWidget().getElement().getStyle().setProperty("marginLeft", "auto");
        policyErrorsLabel.asWidget().getElement().getStyle().setProperty("marginRight", "auto");

        Widget label, selector;

        panel.setWidget(0, 0, label = new Label(i18n.tr("Portfolios:")));
        label.setStyleName(WidgetDecoratorLabel.name());
        panel.getFlexCellFormatter().setWidth(1, 0, "150px");

        panel.setWidget(0, 1, selector = createPortfolioSelector().asWidget());
        selector.getElement().getStyle().setMarginBottom(2, Unit.PX);

        panel.setWidget(1, 0, label = new Label(i18n.tr("Buildings:")));
        label.setStyleName(WidgetDecoratorLabel.name());
        panel.getFlexCellFormatter().setWidth(1, 0, "150px");

        panel.setWidget(1, 1, selector = createBuildingSelector().asWidget());

        panel.setWidget(2, 0, 2, inject(proto().minAmountOwed(), new FieldDecoratorBuilder().componentWidth("150px").labelWidth("150px").build()));
        panel.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);

        panel.setWidget(3, 0, 2, inject(proto().n4PolicyErrors(), policyErrorsLabel));
        panel.getFlexCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);

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
    }

    private IsWidget createPortfolioSelector() {
        return new PortfolioSelector() {//@formatter:off
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
        return new BuildingSelector() {//@formatter:off
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
