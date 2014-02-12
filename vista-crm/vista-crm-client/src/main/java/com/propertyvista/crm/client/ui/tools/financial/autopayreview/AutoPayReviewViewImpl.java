/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.autopayreview;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.reports.autopay.AutoPayChangesReportSettingsForm;
import com.propertyvista.crm.client.ui.tools.common.BulkOperationToolViewImpl;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewViewImpl extends BulkOperationToolViewImpl<AutoPayChangesReportMetadata, PapReviewDTO, PapReviewsHolder> {

    private static final I18n i18n = I18n.get(AutoPayReviewViewImpl.class);

    public AutoPayReviewViewImpl() {
        super(i18n.tr("Auto Pay Reivew"), PapReviewsHolder.class, new PapReviewsHolderForm());
        setSettingsForm(new AutoPayChangesReportSettingsForm(this));
    }

}
