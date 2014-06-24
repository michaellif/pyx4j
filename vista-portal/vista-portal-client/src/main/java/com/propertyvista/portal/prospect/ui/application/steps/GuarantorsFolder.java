/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.domain.person.Person;
import com.propertyvista.portal.rpc.portal.prospect.dto.GuarantorDTO;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.shared.services.dev.MockDataGenerator;

public class GuarantorsFolder extends PortalBoxFolder<GuarantorDTO> {

    private static final I18n i18n = I18n.get(GuarantorsFolder.class);

    public GuarantorsFolder() {
        this(true);
    }

    public GuarantorsFolder(boolean editable) {
        super(GuarantorDTO.class, i18n.tr("Guarantor"), editable);

        if (editable) {
            setNoDataLabel(i18n
                    .tr("Guarantors are individuals who are financially responsible for your lease commitment but will not be living in your apartment"));
        }
    }

    @Override
    protected CForm<GuarantorDTO> createItemForm(IObject<?> member) {
        return new GuarantorForm();
    }

    class GuarantorForm extends CForm<GuarantorDTO> {

        public GuarantorForm() {
            super(GuarantorDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);
            formPanel.append(Location.Left, proto().name().firstName()).decorate();
            formPanel.append(Location.Left, proto().name().lastName()).decorate();
            formPanel.append(Location.Left, proto().relationship()).decorate();
            formPanel.append(Location.Left, proto().email()).decorate();

            return formPanel;
        }

        @Override
        public void generateMockData() {
            GWT.<MockDataGenerator> create(MockDataGenerator.class).getPerson(new DefaultAsyncCallback<Person>() {
                @Override
                public void onSuccess(Person person) {
                    get(proto().name().firstName()).setMockValue(person.name().firstName().getValue());
                    get(proto().name().lastName()).setMockValue(person.name().lastName().getValue());
                    get(proto().email()).setMockValue(person.email().getValue());
                }
            });
        }
    }
}
