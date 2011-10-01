/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.portal.domain.dto.BillDTO;

public class CurrentBillViewImpl extends FlowPanel implements CurrentBillView {

    private final CurrentBillForm billForm;

    private static I18n i18n = I18nFactory.getI18n(PaymentMethodsForm.class);

    public CurrentBillViewImpl() {
        billForm = new CurrentBillForm();
        billForm.initContent();
        add(new VistaHeaderBar(i18n.tr("Current Bill"), "100%"));
        add(billForm);

    }

    @Override
    public void populate(BillDTO bill) {
        billForm.populate(bill);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        billForm.setPresenter(presenter);

    }
}
