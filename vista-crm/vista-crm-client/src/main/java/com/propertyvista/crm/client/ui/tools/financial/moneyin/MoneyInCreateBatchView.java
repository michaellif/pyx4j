/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import java.math.BigDecimal;

import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.site.client.ui.prime.IPrimePane;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;

public interface MoneyInCreateBatchView extends IPrimePane {

    interface Presenter extends IPrimePane.Presenter {

        AbstractDataProvider<MoneyInCandidateDTO> getDataProvider();

        SelectionModel<MoneyInCandidateDTO> getSelectionModel();

        void search();

        void setProcessCandidate(MoneyInCandidateDTO candidate, boolean process);

        void setAmount(MoneyInCandidateDTO candidate, BigDecimal amountToPay);

        void setCheckNumber(MoneyInCandidateDTO candidate, BigDecimal chequeNumber);

        void createBatch();

    }

    void setPresenter(Presenter presenter);

}
