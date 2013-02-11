/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.financial.yardi;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;

public interface FinancialSummaryView extends IsWidget {

    interface Presenter {

        void payNow();

    }

    void setPresenter(Presenter presenter);

    void populate(YardiFinancialSummaryDTO financialSummary);

    void setEnablePayments(boolean eable);

}
