/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4.forms;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.AddressSimple;

public class N4BatchSettingsForm extends CEntityForm<N4BatchRequestDTO> {

    private static final I18n i18n = I18n.get(N4BatchSettingsForm.class);

    private static class MyDecoratorBuilder extends FormDecoratorBuilder {

        public MyDecoratorBuilder(CComponent<?> component) {
            super(component);
        }

        @Override
        public WidgetDecorator build() {
            WidgetDecorator d = super.build();
            d.asWidget().getElement().getStyle().setDisplay(Display.BLOCK);
            return d;
        };

        public Builder width(String componentWidth) {
            contentWidth(componentWidth);
            labelWidth(componentWidth);
            return super.componentWidth(componentWidth);
        }
    }

    private CComboBox<Employee> agentComboBox;

    public N4BatchSettingsForm() {
        super(N4BatchRequestDTO.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, new Label(i18n.tr("The following information will be used to fill N4's:")));
        panel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.CENTER);
        panel.getFlexCellFormatter().addStyleName(row, 0, DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());

        FlowPanel n4FillingSettingsPanel = new FlowPanel();
        n4FillingSettingsPanel.add(new MyDecoratorBuilder(inject(proto().noticeDate())).width("150px").build());
        n4FillingSettingsPanel.add(new MyDecoratorBuilder(inject(proto().deliveryMethod())).width("150px").build());
        n4FillingSettingsPanel.add(new MyDecoratorBuilder(inject(proto().agent(), createAgentComboBox())).width("150px").build());
        panel.setWidget(++row, 0, 2, n4FillingSettingsPanel);

        panel.setWidget(++row, 0, 2, new HTML("&nbsp;"));

        int beforeAddressRow = row;
        row = beforeAddressRow;

        panel.setWidget(++row, 0, new Label(i18n.tr("Agent/Company Mailing Address:")));
        row += 1; // skip one for checkbox in the right column
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().companyName())).build());
        panel.setWidget(++row, 0, inject(proto().mailingAddress(), new AddressSimpleEditor(true)));

        row = beforeAddressRow;
        panel.setWidget(++row, 1, new Label(i18n.tr("Building Owner Mailing Address:")));
        panel.setWidget(++row, 1, new Button(i18n.tr("Use Same Address as Company's"), new Command() {//@formatter:off
            @Override public void execute() { setBuildingOwnerSameAsCompany(); };
        }));//@formatter:on
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().buildingOwnerName())).build());
        panel.setWidget(++row, 1, inject(proto().buildingOwnerMailingAddress(), new AddressSimpleEditor(true)));

        row = beforeAddressRow + 1;
        panel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.CENTER);
        panel.getFlexCellFormatter().getElement(row, 1).getStyle().setTextAlign(TextAlign.CENTER);
        panel.getFlexCellFormatter().addStyleName(row, 0, DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        panel.getFlexCellFormatter().addStyleName(row, 1, DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());

        return panel;
    }

    public void setAgents(List<Employee> agents) {
        agentComboBox.setOptions(agents);
    }

    private void setBuildingOwnerSameAsCompany() {
        get(proto().buildingOwnerMailingAddress()).setValue(get(proto().mailingAddress()).getValue().duplicate(AddressSimple.class));
        get(proto().buildingOwnerMailingAddress()).setValue(get(proto().mailingAddress()).getValue().duplicate(AddressSimple.class));
        get(proto().buildingOwnerName()).setValue(get(proto().companyName()).getValue());
    }

    private CComboBox<Employee> createAgentComboBox() {
        agentComboBox = new CComboBox<Employee>(CComboBox.NotInOptionsPolicy.DISCARD) {
            @Override
            public String getItemName(Employee o) {
                return (o != null && !o.isNull()) ? o.name().getStringView() + (o.signature().getPrimaryKey() == null ? i18n.tr(" (No Signature)") : "") : "";
            }
        };
        agentComboBox.setMandatory(true);
        return agentComboBox;
    }

}
