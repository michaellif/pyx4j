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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.visor.AbstractVisorViewer;

import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.dto.BillDTO;

public class ChargesVisorView extends AbstractVisorViewer<BillDataDTO> {

    private static final I18n i18n = I18n.get(ChargesVisorView.class);

    public ChargesVisorView(ChargesVisorController controller) {
        super(controller);

        setCaption(i18n.tr("Charges"));

        setForm(new ChargesForm());
        getElement().getStyle().setProperty("padding", "6px");
    }

    public void populate(final Command onPopulate) {
        getController().populate(new DefaultAsyncCallback<BillDTO>() {
            @Override
            public void onSuccess(BillDTO result) {
                BillDataDTO dto = EntityFactory.create(BillDataDTO.class);
                dto.bill().set(result);
                populate(dto);
                onPopulate.execute();
            }
        });
    }

    @Override
    public ChargesVisorController getController() {
        return (ChargesVisorController) super.getController();
    }

    private class ChargesForm extends CForm<BillDataDTO> {

        public ChargesForm() {
            super(BillDataDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            DualColumnForm formPanel = new DualColumnForm(this);
            formPanel.append(Location.Left, proto().bill(), new BillForm(true));
            return formPanel;
        }

    }
}
