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
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.property.vendor.Maintenance;

public class CMaintenance extends CDecoratableEntityEditor<Maintenance> {

    public CMaintenance() {
        super(Maintenance.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
//        main.setWidget(++row, 0, inject((Contract) proto().cast(), new CContract()));
//        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setHeader(++row, 0, 2, i18n.tr("Maintenance Schedule"));
        ++row;
        main.setWidget(row, 0, decorate(inject(proto().lastService()), 8.2));
        main.setWidget(row, 1, decorate(inject(proto().nextService()), 8.2));

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }
}
