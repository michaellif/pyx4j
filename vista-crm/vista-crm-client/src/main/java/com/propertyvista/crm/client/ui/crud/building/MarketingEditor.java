/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 21, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;

import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.marketing.MarketingContact;
import com.propertyvista.domain.marketing.MarketingContactEmail;
import com.propertyvista.domain.marketing.MarketingContactPhone;
import com.propertyvista.domain.marketing.MarketingContactUrl;
import com.propertyvista.domain.marketing.ils.ILSOpenHouse;
import com.propertyvista.domain.property.asset.building.Building;

public class MarketingEditor extends CForm<Marketing> {

    private final CForm<? extends Building> parentForm;

    private final AddressStructuredEditor addressEditor = new AddressStructuredEditor(false);

    private AddressStructured emptyAddr;

    public MarketingEditor(CForm<? extends Building> parentForm) {
        super(Marketing.class);
        this.parentForm = parentForm;
    }

    @Override
    public boolean isValid() {
        if (getValue() != null && !getValue().useCustomAddress().getValue(false)) {
            // clear custom address
            if (emptyAddr == null) {
                emptyAddr = EntityFactory.create(AddressStructured.class);
            }
            addressEditor.populate(emptyAddr);
        }
        return super.isValid();
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(160);
        formPanel.append(Location.Right, proto().visibility()).decorate().componentWidth(100);

        formPanel.append(Location.Dual, proto().description()).decorate();

        // marketing address
        formPanel.h1(proto().marketingAddress().getMeta().getCaption());
        formPanel.append(Location.Left, proto().useCustomAddress()).decorate();
        formPanel.append(Location.Dual, proto().marketingAddress(), addressEditor);
        get(proto().useCustomAddress()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setAddressEditorState(event.getValue() == null ? false : event.getValue());
            }
        });

        // marketing contacts
        formPanel.h1(proto().marketingContacts().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().marketingContacts().url(), new MarketingContactEditor<MarketingContactUrl>(MarketingContactUrl.class)));
        formPanel.append(Location.Dual,
                inject(proto().marketingContacts().email(), new MarketingContactEditor<MarketingContactEmail>(MarketingContactEmail.class)));
        formPanel.append(Location.Dual,
                inject(proto().marketingContacts().phone(), new MarketingContactEditor<MarketingContactPhone>(MarketingContactPhone.class)));

        return formPanel;
    }

    private void setAddressEditorState(boolean useCustomizedAddress) {
        if (useCustomizedAddress) {
            addressEditor.setVisible(true);
            if (getValue() == null || getValue().marketingAddress().isNull()) {
                addressEditor.populate((AddressStructured) parentForm.getValue().info().address().duplicate());
            } else {
                addressEditor.populate(getValue().marketingAddress());
            }
        } else {
            addressEditor.setVisible(false);
        }
    }

    @Override
    protected void setEditorValue(Marketing value) {
        // reset address editor state (
        if (value != null) {
            setAddressEditorState(value.useCustomAddress().getValue(false));
        }
        super.setEditorValue(value);
    }

    public static class MarketingContactEditor<T extends MarketingContact> extends CForm<T> {
        public MarketingContactEditor(Class<T> valueClass) {
            super(valueClass);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);

            formPanel.append(Location.Left, proto().value()).decorate();
            formPanel.append(Location.Right, proto().description()).decorate();

            return formPanel;
        }
    }

    private class OpenHouseScheduleFolder extends VistaBoxFolder<ILSOpenHouse> {

        public OpenHouseScheduleFolder() {
            super(ILSOpenHouse.class);
        }

        @Override
        protected CForm<ILSOpenHouse> createItemForm(IObject<?> member) {
            return new ILSOpenHouseEditor();
        }

        private class ILSOpenHouseEditor extends CForm<ILSOpenHouse> {
            public ILSOpenHouseEditor() {
                super(ILSOpenHouse.class);
            }

            @Override
            protected IsWidget createContent() {
                BasicCFormPanel formPanel = new BasicCFormPanel(this);

                formPanel.append(Location.Left, proto().eventDate()).decorate();
                formPanel.append(Location.Left, proto().startTime()).decorate();
                formPanel.append(Location.Left, proto().endTime()).decorate();
                formPanel.append(Location.Left, proto().appointmentRequired()).decorate();

                formPanel.append(Location.Right, proto().details()).decorate();

                return formPanel;
            }
        }
    }
}
