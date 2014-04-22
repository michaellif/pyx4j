/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;

public class BuildingListerGadgetMetadataForm extends CForm<BuildingListerGadgetMetadata> {

    public BuildingListerGadgetMetadataForm() {
        super(BuildingListerGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel p = new TwoColumnFlexFormPanel();
        int row = -1;
        p.setWidget(++row, 0, inject(proto().refreshInterval(), new FieldDecoratorBuilder().build()));
        p.setWidget(++row, 0, inject(proto().buildingListerSettings().pageSize(), new FieldDecoratorBuilder().build()));
        return p;
    }

}
