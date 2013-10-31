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
package com.propertyvista.crm.client.ui.tools.n4generation;

import java.util.List;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityListBox;
import com.pyx4j.forms.client.ui.CListBox.SelectionMode;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationSettingsDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public class N4GenerationSettingsForm extends CEntityForm<N4GenerationSettingsDTO> {

    private static final I18n i18n = I18n.get(N4GenerationSettingsDTO.class);

    private CComboBox<Employee> agentComboBox;

    private final ValueChangeHandler<Boolean> visibilityChangeHandler;

    public N4GenerationSettingsForm() {
        super(N4GenerationSettingsDTO.class);
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
        panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        int row = -1;

        FlowPanel n4FillingSettingsPanel = new FlowPanel();
        n4FillingSettingsPanel.add(new FormDecoratorBuilder(inject(proto().query().noticeDate())).componentWidth("150px").contentWidth("150px").build());
        n4FillingSettingsPanel.add(new FormDecoratorBuilder(inject(proto().query().deliveryMethod())).componentWidth("150px").contentWidth("150px").build());
        agentComboBox = new CComboBox<Employee>("", CComboBox.NotInOptionsPolicy.DISCARD) {
            @Override
            public String getItemName(Employee o) {
                return (o != null && !o.isNull()) ? o.name().getStringView() + (o.signature().getPrimaryKey() == null ? i18n.tr(" (No Signature)") : "") : "";
            }
        };
        agentComboBox.setMandatory(true);
        n4FillingSettingsPanel.add(new FormDecoratorBuilder(inject(proto().query().agent(), agentComboBox)).componentWidth("150px").contentWidth("150px")
                .build());

        panel.setWidget(++row, 0, n4FillingSettingsPanel);

        row = -1;

        FlowPanel leasesQuerySettingsPanel = new FlowPanel();
        leasesQuerySettingsPanel.getElement().getStyle().setOverflow(Overflow.AUTO);

        leasesQuerySettingsPanel.add(new FormDecoratorBuilder(inject(proto().minAmountOwed())).componentWidth("150px").contentWidth("200px").build());
        leasesQuerySettingsPanel.add(new FormDecoratorBuilder(inject(proto().filterByBuildings())).componentWidth("150px").contentWidth("200px").build());

        leasesQuerySettingsPanel.add(new FormDecoratorBuilder(inject(proto().buildings(), new CEntityListBox<Building>(Building.class,
                SelectionMode.SINGLE_PANEL))).useLabelSemicolon(false).customLabel("").componentWidth("200px").contentWidth("200px").build());

        leasesQuerySettingsPanel.add(new FormDecoratorBuilder(inject(proto().filterByPortfolios())).componentWidth("150px").contentWidth("200px").build());
        leasesQuerySettingsPanel.add(new FormDecoratorBuilder(inject(proto().portfolios(), new CEntityListBox<Portfolio>(Portfolio.class,
                SelectionMode.SINGLE_PANEL))).useLabelSemicolon(false).customLabel("").componentWidth("200px").contentWidth("200px").build());

        panel.setWidget(++row, 1, leasesQuerySettingsPanel);

        get(proto().filterByBuildings()).addValueChangeHandler(visibilityChangeHandler);
        get(proto().filterByPortfolios()).addValueChangeHandler(visibilityChangeHandler);
        return panel;
    }

    public void setAgents(List<Employee> agents) {
        agentComboBox.setOptions(agents);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateComponentsVisibility();
    }

    private void updateComponentsVisibility() {
        get(proto().buildings()).setVisible(getValue().filterByBuildings().isBooleanTrue());
        get(proto().portfolios()).setVisible(getValue().filterByPortfolios().isBooleanTrue());
    }

}
