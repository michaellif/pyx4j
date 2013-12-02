/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionState;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInPayerOptionDTO;

public class PayerCandidateSelectionState extends ObjectSelectionState<MoneyInPayerOptionDTO> {

    public PayerCandidateSelectionState(MoneyInCandidateDTO candidate) {
        super(candidate.payerCandidates(), extractSelected(candidate));
    }

    private static MoneyInPayerOptionDTO extractSelected(MoneyInCandidateDTO candidate) {
        MoneyInPayerOptionDTO selected = null;
        if (candidate.payment().payerTenantIdStub().getPrimaryKey() != null) {
            for (MoneyInPayerOptionDTO payerCandidate : candidate.payerCandidates()) {
                if (payerCandidate.tenantIdStub().getPrimaryKey().equals(candidate.payment().payerTenantIdStub().getPrimaryKey())) {
                    selected = payerCandidate;
                    break;
                }
            }
        }
        return selected == null ? candidate.payerCandidates().get(0) : selected;
    }

}
