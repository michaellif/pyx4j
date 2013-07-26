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
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO.PaymentFeeMeasure;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentTypeInfo;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.util.PaymentsSummaryHelper;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentReportServiceImpl implements PaymentReportService {

    @Override
    public void paymentsSummary(AsyncCallback<EntitySearchResult<PaymentsSummary>> callback, Vector<Building> buildingsFilter, LogicalDate targetDate,
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
                    Building building = Persistence.service().retrieve(Building.class, buildingIterator.next().getPrimaryKey(), AttachLevel.ToStringMembers);
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
    public void paymentsFees(AsyncCallback<Vector<PaymentFeesDTO>> callback) {
        // TODO: WARNING getCurrentPmc() uses current namespace to get currentPmc:        
        final Pmc currentPmc = VistaDeployment.getCurrentPmc();

        PmcPaymentTypeInfo paymentTypeInfo = TaskRunner.runInOperationsNamespace(new Callable<PmcPaymentTypeInfo>() {
            @Override
            public PmcPaymentTypeInfo call() throws Exception {
                EntityQueryCriteria<PmcPaymentTypeInfo> criteria = EntityQueryCriteria.create(PmcPaymentTypeInfo.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), currentPmc));
                return Persistence.service().retrieve(criteria);
            }
        });

        Vector<PaymentFeesDTO> paymentFees = new Vector<PaymentFeesDTO>();
        if (paymentTypeInfo != null) {
            paymentFees.add(PaymentFeesHelper.extractFees(paymentTypeInfo, PaymentFeeMeasure.absolute));
            paymentFees.add(PaymentFeesHelper.extractFees(paymentTypeInfo, PaymentFeeMeasure.relative));
        }
        callback.onSuccess(paymentFees);
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
