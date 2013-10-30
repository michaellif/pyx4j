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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.reports.eft.SelectedBuildingsFolder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationSettingsDTO;
import com.propertyvista.domain.company.Employee;

public class N4GenerationSettingsForm extends CEntityDecoratableForm<N4GenerationSettingsDTO> {

    private static final I18n i18n = I18n.get(N4GenerationSettingsDTO.class);

    private CComboBox<Employee> agentComboBox;

    public N4GenerationSettingsForm() {
        super(N4GenerationSettingsDTO.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().query().noticeDate())).componentWidth("150px").build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().query().deliveryMethod())).componentWidth("150px").build());
        agentComboBox = new CComboBox<Employee>("", CComboBox.NotInOptionsPolicy.DISCARD) {
            @Override
            public String getItemName(Employee o) {
                return (o != null && !o.isNull()) ? o.name().getStringView() + (o.signature().getPrimaryKey() == null ? i18n.tr(" (No Signature)") : "") : "";
            }
        };
        agentComboBox.setMandatory(true);
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().query().agent(), agentComboBox)).componentWidth("150px").build());

        row = -1;

        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().minAmountOwed())).componentWidth("150px").build());

        FlowPanel buildingSelectionPanel = new FlowPanel();
        buildingSelectionPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
        buildingSelectionPanel.add(new FormDecoratorBuilder(inject(proto().filterByBuildings())).build());
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().buildings()).setVisible(event.getValue() == true);
            }
        });
        buildingSelectionPanel.add(inject(proto().buildings(), new SelectedBuildingsFolder()));
        if (false) {
            panel.setWidget(++row, 1, buildingSelectionPanel);
        }

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().buildings()).setVisible(getValue().filterByBuildings().isBooleanTrue());
    }

    public void setAgents(List<Employee> agents) {
        agentComboBox.setOptions(agents);
    }
}
