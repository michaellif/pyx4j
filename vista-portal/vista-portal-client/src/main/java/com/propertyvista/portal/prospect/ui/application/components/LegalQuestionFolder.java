/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 15, 2014
 * @author VladL
 */
package com.propertyvista.portal.prospect.ui.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestion;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class LegalQuestionFolder extends PortalBoxFolder<CustomerScreeningLegalQuestion> {

    public LegalQuestionFolder() {
        super(CustomerScreeningLegalQuestion.class, false);
        setExpended(true);
    }

    @Override
    protected CForm<CustomerScreeningLegalQuestion> createItemForm(IObject<?> member) {
        return new CForm<CustomerScreeningLegalQuestion>(CustomerScreeningLegalQuestion.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().question()).decorate();

                formPanel.append(Location.Left, proto().answer()).decorate();
                formPanel.append(Location.Left, proto().notes()).decorate();

                return formPanel;
            }

            @Override
            public void generateMockData() {
                get(proto().answer()).setMockValue(false);
            }
        };
    }
}
