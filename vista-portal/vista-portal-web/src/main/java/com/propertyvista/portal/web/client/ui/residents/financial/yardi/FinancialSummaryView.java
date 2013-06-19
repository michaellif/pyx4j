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
package com.propertyvista.portal.web.client.ui.residents.financial.yardi;

import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;
import com.propertyvista.portal.web.client.ui.residents.View;

public interface FinancialSummaryView extends View<YardiFinancialSummaryDTO> {

    interface Presenter extends View.Presenter<YardiFinancialSummaryDTO> {

        void payNow();
    }

    void setEnablePayments(boolean eable);
}
