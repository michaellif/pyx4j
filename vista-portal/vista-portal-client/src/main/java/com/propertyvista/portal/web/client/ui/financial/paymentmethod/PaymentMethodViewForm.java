/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.paymentmethod;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;

public class PaymentMethodViewForm extends CPortalEntityForm<LeasePaymentMethod> {

    private static final I18n i18n = I18n.get(PaymentMethodViewForm.class);

    public PaymentMethodViewForm(PaymentMethodViewImpl view) {
        super(LeasePaymentMethod.class, view, i18n.tr("Payment Method"), ThemeColor.contrast4);

        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setH1(++row, 0, 1, i18n.tr("Payment Method Editor Form goes here..."));

        return content;
    }

    @Override
    protected FormDecorator<LeasePaymentMethod, CEntityForm<LeasePaymentMethod>> createDecorator() {
        FormDecorator<LeasePaymentMethod, CEntityForm<LeasePaymentMethod>> decorator = super.createDecorator();

        return decorator;
    }
}
