/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-11
 * @author Vlad
 */
package com.propertyvista.portal.shared.ui.util.editors;

import java.text.ParseException;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IParser;
import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Name.Prefix;
import com.propertyvista.portal.shared.ui.AccessoryEntityForm;
import com.propertyvista.shared.config.VistaFeatures;

public class NameEditor extends AccessoryEntityForm<Name> {

    private final CField<Name, ?> nameComp;

    private final String customViewLabel;

    public NameEditor(String customViewLabel) {
        super(Name.class);
        this.customViewLabel = customViewLabel;

        nameComp = new CEntityLabel<Name>();
        nameComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        adopt(nameComp);
    }

    /**
     * overwrite to supply real entity key for hyperlink
     */
    public Key getLinkKey() {
        return getValue().getPrimaryKey();
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, nameComp).decorate().customLabel(customViewLabel).componentWidth(200);
        formPanel.append(Location.Left, proto().firstName()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().lastName()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().middleName()).decorate().componentWidth(60);
        formPanel.append(Location.Left, proto().namePrefix()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().nameSuffix()).decorate().componentWidth(60);

        calculateFieldsStatus();

        get(proto().firstName()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                ValueChangeEvent.fire(NameEditor.this, NameEditor.this.getValue());
            }
        });
        get(proto().lastName()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                ValueChangeEvent.fire(NameEditor.this, NameEditor.this.getValue());
            }
        });
        get(proto().middleName()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                ValueChangeEvent.fire(NameEditor.this, NameEditor.this.getValue());
            }
        });
        get(proto().namePrefix()).addValueChangeHandler(new ValueChangeHandler<Prefix>() {
            @Override
            public void onValueChange(ValueChangeEvent<Prefix> event) {
                ValueChangeEvent.fire(NameEditor.this, NameEditor.this.getValue());
            }
        });
        get(proto().nameSuffix()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                ValueChangeEvent.fire(NameEditor.this, NameEditor.this.getValue());
            }
        });

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.viewable)) {
                    calculateFieldsStatus();
                }
            }
        });

        // abbreviate middle name in case of Yardi:
        if (VistaFeatures.instance().yardiIntegration()) {
            @SuppressWarnings("unchecked")
            CTextComponent<String, ?> mnComp = ((CTextComponent<String, ?>) get(proto().middleName()));
            mnComp.setParser(new IParser<String>() {
                @Override
                public String parse(String string) throws ParseException {
                    return (!string.isEmpty() ? string.substring(0, 1).toUpperCase() + '.' : string);
                }
            });
        }

        return formPanel;
    }

    @Override
    public void generateMockData() {
        get(proto().firstName()).setMockValue("Jane");
        get(proto().lastName()).setMockValue("Doe");
    }

    private void calculateFieldsStatus() {
        if (isViewable()) {
            nameComp.setVisible(true);

            get(proto().firstName()).setVisible(false);
            get(proto().lastName()).setVisible(false);
            get(proto().middleName()).setVisible(false);
            get(proto().namePrefix()).setVisible(false);
            get(proto().nameSuffix()).setVisible(false);
        } else {
            nameComp.setVisible(false);

            get(proto().firstName()).setVisible(true);
            get(proto().lastName()).setVisible(true);
            get(proto().middleName()).setVisible(true);
            get(proto().namePrefix()).setVisible(true);
            get(proto().nameSuffix()).setVisible(true);
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isViewable()) {
            nameComp.populate(getValue());
        }
    }
}
