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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityListBox;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CListBox.SelectionMode;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public class N4CandidateSearchCriteriaForm extends CEntityForm<N4CandidateSearchCriteriaDTO> {

    private final ValueChangeHandler<Boolean> visibilityChangeHandler;

    private CLabel<String> policyErrorsLabel;

    private BasicFlexFormPanel searchCriteriaPanel;

    public N4CandidateSearchCriteriaForm() {
        super(N4CandidateSearchCriteriaDTO.class);
        visibilityChangeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                updateComponentsVisibility();
            }
        };
    }

    @Override
    public IsWidget createContent() {
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

        searchCriteriaPanel = new BasicFlexFormPanel();
        searchCriteriaPanel.setWidget(row, 0, 1, new MyDecoratorBuilder(inject(proto().minAmountOwed())).componentWidth("150px").contentWidth("200px").build());
        searchCriteriaPanel.setWidget(row, 1, 1, new MyDecoratorBuilder(inject(proto().filterByBuildings())).componentWidth("150px").contentWidth("200px")
                .build());
        searchCriteriaPanel.setWidget(++row, 1, 1,
                new MyDecoratorBuilder(inject(proto().buildings(), new CEntityListBox<Building>(Building.class, SelectionMode.SINGLE_PANEL)))
                        .useLabelSemicolon(false).customLabel("").componentWidth("200px").contentWidth("200px").build());

        searchCriteriaPanel.setWidget(++row, 1, 1, new MyDecoratorBuilder(inject(proto().filterByPortfolios())).componentWidth("150px").contentWidth("200px")
                .build());
        searchCriteriaPanel.setWidget(++row, 1, 1,
                new MyDecoratorBuilder(inject(proto().portfolios(), new CEntityListBox<Portfolio>(Portfolio.class, SelectionMode.SINGLE_PANEL)))
                        .useLabelSemicolon(false).customLabel("").componentWidth("200px").contentWidth("200px").build());

        panel.setWidget(1, 0, 2, searchCriteriaPanel);

        get(proto().filterByBuildings()).addValueChangeHandler(visibilityChangeHandler);
        get(proto().filterByPortfolios()).addValueChangeHandler(visibilityChangeHandler);
        return panel;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        addValueValidator(new EditableValueValidator<N4CandidateSearchCriteriaDTO>() {
            @Override
            public ValidationError isValid(CComponent<N4CandidateSearchCriteriaDTO> component, N4CandidateSearchCriteriaDTO value) {
                if (value != null && !CommonsStringUtils.isEmpty(value.n4PolicyErrors().getValue())) {
                    return new ValidationError(component, value.n4PolicyErrors().getValue());
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
        get(proto().buildings()).setVisible(getValue().filterByBuildings().isBooleanTrue());
        get(proto().portfolios()).setVisible(getValue().filterByPortfolios().isBooleanTrue());
        policyErrorsLabel.setVisible(!CommonsStringUtils.isEmpty(getValue().n4PolicyErrors().getValue()));
        searchCriteriaPanel.setVisible(CommonsStringUtils.isEmpty(getValue().n4PolicyErrors().getValue()));
    }

    private static class MyDecoratorBuilder extends FormDecoratorBuilder {

        public MyDecoratorBuilder(CComponent<?> component) {
            super(component);
        }

        @Override
        public WidgetDecorator build() {
            WidgetDecorator d = super.build();
            d.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            return d;
        };
    }
}
