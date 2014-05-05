/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;

import com.propertyvista.common.client.ui.components.folders.CompanyPhoneFolder;
import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.domain.company.Company;

public class CompanyEditor extends CForm<Company> {

    public CompanyEditor() {
        super(Company.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(180);

// TODO : design representation for:
//      main.add(parent.inject(proto.addresses()), 15);

        formPanel.append(Location.Dual, inject(proto().phones(), new CompanyPhoneFolder(isEditable())));
        formPanel.append(Location.Left, proto().website()).decorate().componentWidth(250);

        formPanel.append(Location.Dual, inject(proto().emails(), new EmailFolder(isEditable())));

//TODO : design representation for:
//      main.add(parent.inject(proto.contacts()), 15);
//      main.add(parent.inject(proto.logo()), 15);

        return formPanel;
    }
}
