/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.payment;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.wizard.VistaAbstractWizard;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentWizardViewImpl extends VistaAbstractWizard<PaymentRecordDTO> implements PaymentWizardView {

    private static final I18n i18n = I18n.get(PaymentWizardViewImpl.class);

    public PaymentWizardViewImpl() {
        super(i18n.tr("Payment Setup"));
        setForm(new PaymentWizardForm(this));

        setEndButtonCaption(i18n.tr("Submit"));
    }
}
