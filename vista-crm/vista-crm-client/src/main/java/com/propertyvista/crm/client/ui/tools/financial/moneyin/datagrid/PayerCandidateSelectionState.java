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
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionState;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInLeaseParticipantDTO;

public class PayerCandidateSelectionState extends ObjectSelectionState<MoneyInLeaseParticipantDTO> {

    public PayerCandidateSelectionState(MoneyInCandidateDTO candidate) {
        super(candidate.payerCandidates(), extractSelected(candidate));
    }

    private static MoneyInLeaseParticipantDTO extractSelected(MoneyInCandidateDTO candidate) {
        MoneyInLeaseParticipantDTO selected = null;
        if (candidate.payment().payerLeaseTermTenantIdStub().getPrimaryKey() != null) {
            for (MoneyInLeaseParticipantDTO payerCandidate : candidate.payerCandidates()) {
                if (payerCandidate.leaseTermTenantIdStub().getPrimaryKey().equals(candidate.payment().payerLeaseTermTenantIdStub().getPrimaryKey())) {
                    selected = payerCandidate;
                    break;
                }
            }
        }
        return selected == null ? candidate.payerCandidates().get(0) : selected;
    }

}
