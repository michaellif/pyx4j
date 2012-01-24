/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.ApplicationStatusFolder;
import com.propertyvista.dto.MasterApplicationStatusDTO;

public class ApplicationStatusViewForm extends CEntityDecoratableEditor<MasterApplicationStatusDTO> {

    public ApplicationStatusViewForm() {
        super(MasterApplicationStatusDTO.class);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().progress()), 5).labelWidth(20).build());
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().individualApplications(), new ApplicationStatusFolder(isEditable())));

        return main;
    }
}
