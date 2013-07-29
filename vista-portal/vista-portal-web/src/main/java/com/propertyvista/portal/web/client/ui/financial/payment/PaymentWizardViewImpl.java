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
package com.propertyvista.portal.web.client.ui.financial.payment;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.web.client.ui.AbstractWizardView;

public class PaymentWizardViewImpl extends AbstractWizardView<PaymentRecordDTO> implements PaymentWizardView {

    private static final I18n i18n = I18n.get(PaymentWizardViewImpl.class);

    public PaymentWizardViewImpl() {
        super();
        setWizard(new PaymentWizardForm(this, i18n.tr("Submit")));

    }
}
