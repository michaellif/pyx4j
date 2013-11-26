/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.AbstractWizard;

import com.propertyvista.crm.rpc.dto.legal.l1.L1FormDataReviewWizardDTO;

public class L1FormDataReviewWizardViewImpl extends AbstractWizard<L1FormDataReviewWizardDTO> implements L1FormDataReviewWizardView {

    private static final I18n i18n = I18n.get(L1FormDataReviewWizardViewImpl.class);

    public L1FormDataReviewWizardViewImpl() {
        super(i18n.tr("L1 Form Review"));
        setForm(new L1FormDataReviewWizardForm(this));
    }

}
