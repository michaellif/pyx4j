/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;

import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;

public interface OnlinePaymentWizardView extends IWizard<OnlinePaymentSetupDTO>, IsView {

    interface Persenter extends IWizard.Presenter {

    }

    void setPaymentFees(AbstractPaymentFees paymentFees);

}
