/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class CollectionsGadgetServiceImpl implements CollectionsGadgetService {

    @Override
    public void countData(AsyncCallback<CollectionsGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        CollectionsGadgetDataDTO data = EntityFactory.create(CollectionsGadgetDataDTO.class);

        count(data.leasesPaidThisMonth(), buildingsFilter);

        summarize(data.fundsCollectedThisMonth(), buildingsFilter);
        summarize(data.fundsInProcessing(), buildingsFilter);

        callback.onSuccess(data);
    }

    @Override
    public void makePaymentCriteria(AsyncCallback<EntityListCriteria<PaymentRecordDTO>> callback, Vector<Building> buildingsFilter, String filter) {
        callback.onSuccess(paymentRecordsCriteria(EntityListCriteria.create(PaymentRecordDTO.class), buildingsFilter, filter));
    }

    @Override
    public void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter, String filter) {
        callback.onSuccess(leasesCriteria(EntityListCriteria.create(LeaseDTO.class), buildingsFilter));
    }

    /**
     * @return a criteria that should find all the tenants who payed during the month defined by the transaction time
     */
    private <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria leasesCriteria(Criteria criteria, Vector<Building> buildingsFilter) {
        LogicalDate today = Util.dayOfCurrentTransaction();
        LogicalDate thisMonthStartDay = Util.beginningOfMonth(today);

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildingsFilter));
        }
        criteria.add(PropertyCriterion.ne(criteria.proto().billingAccount().payments().$().paymentStatus(), PaymentRecord.PaymentStatus.Submitted));
        criteria.add(PropertyCriterion.ne(criteria.proto().billingAccount().payments().$().paymentStatus(), PaymentRecord.PaymentStatus.Canceled));
        criteria.add(PropertyCriterion.ne(criteria.proto().billingAccount().payments().$().paymentStatus(), PaymentRecord.PaymentStatus.Rejected));
        criteria.add(PropertyCriterion.ge(criteria.proto().billingAccount().payments().$().createdDate(), thisMonthStartDay));
        criteria.add(PropertyCriterion.le(criteria.proto().billingAccount().payments().$().createdDate(), today));

        return criteria;
    }

    private <Criteria extends EntityQueryCriteria<? extends PaymentRecord>> Criteria paymentRecordsCriteria(Criteria criteria,
            Vector<Building> buildingsFilter, String fundsFilter) {

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().building(), buildingsFilter));
        }

        CollectionsGadgetDataDTO proto = EntityFactory.getEntityPrototype(CollectionsGadgetDataDTO.class);
        IObject<?> fundsFilterProto = proto.getMember(new Path(fundsFilter));

        LogicalDate today = Util.dayOfCurrentTransaction();
        LogicalDate thisMonthStartDay = Util.beginningOfMonth(today);

        criteria.add(PropertyCriterion.ge(criteria.proto().createdDate(), thisMonthStartDay));
        criteria.add(PropertyCriterion.le(criteria.proto().createdDate(), today));

        criteria.add(PropertyCriterion.ne(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Submitted));
        criteria.add(PropertyCriterion.ne(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Canceled));
        criteria.add(PropertyCriterion.ne(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Rejected));
        if (proto.fundsCollectedThisMonth() == fundsFilterProto) {

        } else if (proto.fundsInProcessing() == fundsFilterProto) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentStatus(), EnumSet.complementOf(EnumSet.copyOf(PaymentRecord.PaymentStatus.processed()))));
        } else {
            throw new RuntimeException("unknown filter preset: " + fundsFilter);
        }

        return criteria;
    }

    private void count(IPrimitive<Integer> member, Vector<Building> buildingsFilter) {

        CollectionsGadgetDataDTO proto = EntityFactory.getEntityPrototype(CollectionsGadgetDataDTO.class);
        IObject<?> filter = proto.getMember(member.getPath());

        if (proto.leasesPaidThisMonth() == filter) {
            EntityQueryCriteria<Lease> criteria = leasesCriteria(EntityQueryCriteria.create(Lease.class), buildingsFilter);
            Persistence.applyDatasetAccessRule(criteria);
            member.setValue(Persistence.service().count(criteria));
        } else {
            throw new RuntimeException("unknown filter preset: " + member.getPath().toString());
        }
    }

    private void summarize(IPrimitive<BigDecimal> member, Vector<Building> buildingsFilter) {
        BigDecimal sum = new BigDecimal("0.00");
        EntityQueryCriteria<PaymentRecord> criteria = paymentRecordsCriteria(EntityQueryCriteria.create(PaymentRecord.class), buildingsFilter, member.getPath()
                .toString());

        ICursorIterator<PaymentRecord> i = null;
        try {
            Persistence.applyDatasetAccessRule(criteria);
            i = Persistence.service().query(null, criteria, AttachLevel.Attached);
            while (i.hasNext()) {
                sum = sum.add(i.next().amount().getValue());
            }
        } finally {
            IOUtils.closeQuietly(i);
        }
        member.setValue(sum);

    }
}
