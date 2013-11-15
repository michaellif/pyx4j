/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.views.bill;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillViewDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class BillViewForm extends CPortalEntityForm<BillViewDTO> {

    private static final I18n i18n = I18n.get(BillViewForm.class);

    public BillViewForm(BillView view) {
        super(BillViewDTO.class, view, i18n.tr("Bill View"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().billData(), new BillForm(/* true */)));

        return content;
    }

    @Override
    protected FormDecorator<BillViewDTO, CEntityForm<BillViewDTO>> createDecorator() {
        FormDecorator<BillViewDTO, CEntityForm<BillViewDTO>> decorator = super.createDecorator();

        Button btnPay = new Button(i18n.tr("Pay Bill"), new Command() {
            @Override
            public void execute() {
                ((BillView.Presenter) getView().getPresenter()).payBill();
            }
        });
        decorator.addHeaderToolbarWidget(btnPay);

        return decorator;
    }
}
