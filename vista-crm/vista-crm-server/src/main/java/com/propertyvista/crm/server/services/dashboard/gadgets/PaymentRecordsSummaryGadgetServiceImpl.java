/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsSummaryGadgetService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO.PaymentFeePolicy;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesHolderDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.util.PaymentsSummaryHelper;

public class PaymentRecordsSummaryGadgetServiceImpl implements PaymentRecordsSummaryGadgetService {

    @Override
    public void paymentRecordsSummary(AsyncCallback<EntitySearchResult<PaymentsSummary>> callback, Vector<Building> buildingsFilter, LogicalDate targetDate,
            Vector<PaymentStatus> paymentStatusCriteria, int pageNumber, int pageSize, Vector<Sort> sortingCriteria) {

        Vector<PaymentsSummary> summariesVector = new Vector<PaymentsSummary>();

        PaymentsSummaryHelper summaryHelper = new PaymentsSummaryHelper();

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        if (!buildingsFilter.isEmpty()) {
            buildingCriteria.in(buildingCriteria.proto().id(), buildingsFilter);
        }
        Iterator<Building> buildingIterator = Persistence.secureQuery(null, buildingCriteria, AttachLevel.IdOnly);
        try {
            if (PaymentsSummary.summaryByBuilding) {
                while (buildingIterator.hasNext()) {
                    Building building = Persistence.service().retrieve(Building.class, buildingIterator.next().getPrimaryKey(), AttachLevel.ToStringMembers, false);
                    for (PaymentStatus paymentStatus : paymentStatusCriteria) {
                        PaymentsSummary summary = summaryHelper.calculateSummary(building, paymentStatus, targetDate);
                        if (summaryHelper.hasPayments(summary)) {
                            summariesVector.add(summary);
                        }
                        summary.building().set(building);
                    }
                }
            } else {
                Iterator<MerchantAccount> merchantAccounts = merchantAccountIterator(buildingIterator);
                while (merchantAccounts.hasNext()) {
                    MerchantAccount merchantAccount = merchantAccounts.next();
                    for (PaymentStatus paymentStatus : paymentStatusCriteria) {
                        PaymentsSummary summary = summaryHelper.calculateSummary(merchantAccount, paymentStatus, targetDate);
                        if (summaryHelper.hasPayments(summary)) {
                            summariesVector.add(summary);
                        }
                    }
                }
                // load detached merchant accounts
                for (PaymentsSummary paymentsSummary : summariesVector) {
                    paymentsSummary.merchantAccount().set(
                            Persistence.service().retrieve(MerchantAccount.class, paymentsSummary.merchantAccount().getPrimaryKey()));
                }
            }
        } finally {
            IOUtils.closeQuietlyIfCloseable(buildingIterator);
        }

        EntityListCriteria<PaymentsSummary> paymentsCriteria = EntityListCriteria.create(PaymentsSummary.class);
        paymentsCriteria.setSorts(sortingCriteria);
        paymentsCriteria.setPageNumber(pageNumber);
        paymentsCriteria.setPageSize(pageSize);

        InMemeoryListService<PaymentsSummary> inMemoryService = new InMemeoryListService<PaymentsSummary>(summariesVector);
        inMemoryService.list(callback, paymentsCriteria);
    }

    @Override
    public void fundsTransferFees(AsyncCallback<PaymentFeesHolderDTO> callback) {

        AbstractPaymentFees fees = ServerSideFactory.create(Vista2PmcFacade.class).getPaymentFees();
        PaymentFeesHolderDTO paymentFeesHolder = EntityFactory.create(PaymentFeesHolderDTO.class);
        PaymentFeesDTO paymentFeesDto = EntityFactory.create(PaymentFeesDTO.class);
        paymentFeesDto.paymentFeePolicy().setValue(PaymentFeePolicy.feePerStransaction);
        paymentFeesDto.visa().setValue(fees.ccVisaFee().getValue());
        paymentFeesDto.visaDebit().setValue(fees.visaDebitFee().getValue());
        paymentFeesDto.masterCard().setValue(fees.ccMasterCardFee().getValue());
        paymentFeesDto.eCheck().setValue(fees.eChequeFee().getValue());
        paymentFeesDto.directBanking().setValue(fees.directBankingFee().getValue());
        paymentFeesHolder.paymentFees().add(paymentFeesDto);

        callback.onSuccess(paymentFeesHolder);
    }

    /** returns iterator over merchant accounts of the given buildings */
    private static Iterator<MerchantAccount> merchantAccountIterator(Iterator<Building> buildingIterator) {
        List<MerchantAccount> merchantAccounts = new LinkedList<MerchantAccount>();
        Set<Key> alreadyAddedMerchantAccounts = new HashSet<Key>();

        while (buildingIterator.hasNext()) {
            EntityQueryCriteria<BuildingMerchantAccount> criteria = EntityQueryCriteria.create(BuildingMerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), buildingIterator.next()));

            List<BuildingMerchantAccount> buildingMerchantAccounts = Persistence.service().query(criteria, AttachLevel.Detached);
            for (BuildingMerchantAccount buildingMerchantAccount : buildingMerchantAccounts) {
                if (alreadyAddedMerchantAccounts.add(buildingMerchantAccount.merchantAccount().getPrimaryKey())) {
                    merchantAccounts.add(buildingMerchantAccount.merchantAccount().<MerchantAccount> detach());
                }
            }
        }
        return merchantAccounts.iterator();
    }

}
