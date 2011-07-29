/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.admin.client.ui.components.AdminEditorsComponentFactory;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;

public class PmcEditorFormNewItem extends AdminEntityForm<PmcDTO> {

    public PmcEditorFormNewItem() {
        super(PmcDTO.class, new AdminEditorsComponentFactory());
    }

    public PmcEditorFormNewItem(IEditableComponentFactory factory) {
        super(PmcDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().name()), 15);
        main.add(inject(proto().dnsName()), 15);
        main.add(inject(proto().email()), 15);
        main.add(inject(proto().password()), 15);

        main.setWidth("100%");
        return main;
    }

}