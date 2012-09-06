/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 6, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.charges;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.dto.BillDTO;

public class ChargesVisorView extends ScrollPanel {

    private static final I18n i18n = I18n.get(ChargesVisorView.class);

    private final ChargesVisorController controller;

    private final CrmEntityForm<BillDataDTO> form;

    public ChargesVisorView(ChargesVisorController controller) {
        this.controller = controller;

        // UI:
        form = new ChargesForm();
        form.initContent();

        setWidget(form.asWidget());
        getElement().getStyle().setProperty("padding", "6px");
    }

    public void populate(final Command onPopulate) {
        controller.populate(new DefaultAsyncCallback<BillDTO>() {
            @Override
            public void onSuccess(BillDTO result) {
                BillDataDTO dto = EntityFactory.create(BillDataDTO.class);
                dto.bill().set(result);
                form.populate(dto);
                onPopulate.execute();
            }
        });
    }

    public ChargesVisorController getController() {
        return controller;
    }

    private class ChargesForm extends CrmEntityForm<BillDataDTO> {

        public ChargesForm() {
            super(BillDataDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            main.setWidget(0, 0, inject(proto().bill(), new BillForm(true)));

            return main;
        }

        @Override
        protected void createTabs() {
            // TODO Auto-generated method stub
        }
    }
}
