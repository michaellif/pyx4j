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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormat;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.n4.N4PolicyForm;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.company.Employee;

public class N4BatchRequestForm extends CEntityForm<N4BatchRequestDTO> {

    private static final I18n i18n = I18n.get(N4BatchRequestForm.class);

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

    public N4BatchRequestForm() {
        super(N4BatchRequestDTO.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        FlowPanel n4FillingSettingsPanel = new FlowPanel();
        n4FillingSettingsPanel.add(new MyDecoratorBuilder(inject(proto().noticeDate())).width("150px").build());
        n4FillingSettingsPanel.add(new MyDecoratorBuilder(inject(proto().deliveryMethod())).width("150px").build());
        n4FillingSettingsPanel.add(new MyDecoratorBuilder(inject(proto().agent(), createAgentComboBox())).width("150px").build());
        panel.setWidget(++row, 0, 2, n4FillingSettingsPanel);

        panel.setWidget(++row, 0, 2, new HTML("&nbsp;"));

        panel.setH1(++row, 0, 2, i18n.tr("Agent/Company Contact Information:"));
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().companyName())).build());
        CPhoneField phoneNumberField = inject(proto().phoneNumber(), new CPhoneField() {
            @Override
            public IFormat<String> getFormat() {
                return N4PolicyForm.PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT;
            }
        });
        phoneNumberField.setWatermark("(___) ___-____");
        phoneNumberField.setFormat(N4PolicyForm.PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT); // TODO y setFormat not working?
        panel.setWidget(++row, 0, new FormDecoratorBuilder(phoneNumberField).build());

        CPhoneField faxNumberField = inject(proto().faxNumber(), new CPhoneField() {
            @Override
            public IFormat<String> getFormat() {
                return N4PolicyForm.PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT;
            }
        });
        faxNumberField.setWatermark("(___) ___-____");
        faxNumberField.setFormat(N4PolicyForm.PHONE_NUMBER_WITHOUT_EXTENSION_FORMAT); // TODO y setFormat not working?
        panel.setWidget(++row, 0, new FormDecoratorBuilder(faxNumberField).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().emailAddress())).build());
        panel.setWidget(++row, 0, inject(proto().mailingAddress(), new AddressSimpleEditor(true)));

        return panel;
    }

    public void setAgents(List<Employee> agents) {
        agentComboBox.setOptions(agents);
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
