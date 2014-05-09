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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.company.Employee;

public class N4BatchRequestForm extends CForm<N4BatchRequestDTO> {

    private static final I18n i18n = I18n.get(N4BatchRequestForm.class);

    private static class MyDecoratorBuilder extends FieldDecoratorBuilder {

        public MyDecoratorBuilder() {
            super();
        }

        @Override
        public FieldDecorator build() {
            FieldDecorator d = super.build();
            d.asWidget().getElement().getStyle().setDisplay(Display.BLOCK);
            return d;
        };

        public Builder width(String componentWidth) {
            labelWidth(componentWidth);
            return super.componentWidth(componentWidth);
        }
    }

    private CComboBox<Employee> agentComboBox;

    public N4BatchRequestForm() {
        super(N4BatchRequestDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        FlowPanel n4FillingSettingsPanel = new FlowPanel();
        n4FillingSettingsPanel.add(inject(proto().noticeDate(), new MyDecoratorBuilder().width("150px").build()));
        n4FillingSettingsPanel.add(inject(proto().deliveryMethod(), new MyDecoratorBuilder().width("150px").build()));
        n4FillingSettingsPanel.add(inject(proto().agent(), createAgentComboBox(), new MyDecoratorBuilder().width("150px").build()));
        formPanel.append(Location.Dual, n4FillingSettingsPanel);
        formPanel.br();
        formPanel.h1(i18n.tr("Agent/Company Contact Information:"));
        formPanel.append(Location.Left, proto().companyName()).decorate();
        formPanel.append(Location.Left, proto().phoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        formPanel.append(Location.Left, proto().faxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        formPanel.append(Location.Left, proto().emailAddress()).decorate();
        formPanel.append(Location.Dual, proto().mailingAddress(), new AddressStructuredEditor(true));

        return formPanel;
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
