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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.property.vendor.Contract;

public class CContract extends CEntityDecoratableEditor<Contract> {

    public CContract() {
        super(Contract.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contractID()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contractor()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cost()), 10).build());

// TODO : design representation for:
//      main.setWidget(++row, 0, decorate(inject(proto.document()), 50);

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().start()), 8.2).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().end()), 8.2).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }
}
