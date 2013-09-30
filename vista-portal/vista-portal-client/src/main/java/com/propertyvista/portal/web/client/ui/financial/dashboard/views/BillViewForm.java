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
package com.propertyvista.portal.web.client.ui.financial.dashboard.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillViewDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;

public class BillViewForm extends CPortalEntityForm<BillViewDTO> {

    private static final I18n i18n = I18n.get(BillViewForm.class);

    public BillViewForm() {
        super(BillViewDTO.class, null, i18n.tr("Bill View"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().billData(), new BillForm(true, true)));

        return content;
    }
}
