/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;

import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.dto.vista2pmc.CreditCheckSetupDTO;

public interface CreditCheckWizardView extends IWizard<CreditCheckSetupDTO>, IsView {

    interface Presenter extends IWizard.Presenter {
    }

    void setCreditCheckFees(AbstractEquifaxFee creditCheckFees);
}
