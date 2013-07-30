/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.PmcMerchantAccountDTO;

public interface PmcViewerView extends IViewer<PmcDTO> {

    interface Presenter extends IViewer.Presenter {

        void resetCache();

        void activate();

        void suspend();

        void cancelPmc();

        void runProcess(PmcProcessType pmcProcessType, ScheduleDataDTO date);

        ListerDataSource<PmcMerchantAccountDTO> getOnboardingMerchantAccountsSource();

        ListerDataSource<DirectDebitRecord> getDirectDebitRecordsSource();
    }

}
