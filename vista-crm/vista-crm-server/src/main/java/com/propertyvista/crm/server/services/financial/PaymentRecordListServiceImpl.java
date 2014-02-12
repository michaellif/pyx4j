/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentRecordListServiceImpl extends AbstractCrudServiceDtoImpl<PaymentRecord, PaymentRecordDTO> implements PaymentRecordListService {

    public PaymentRecordListServiceImpl() {
        super(PaymentRecord.class, PaymentRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, PaymentRecord boProto, PaymentRecordDTO toProto) {
        if (path.equals(toProto.rejectedWithNSF().getPath().toString())) {
            return boProto.invoicePaymentBackOut().applyNSF().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<PaymentRecord> boCriteria, EntityListCriteria<PaymentRecordDTO> toCriteria) {
        PropertyCriterion nsfCriteria = toCriteria.getCriterion(toCriteria.proto().rejectedWithNSF());
        if (nsfCriteria != null) {
            toCriteria.getFilters().remove(nsfCriteria);
            boCriteria.eq(boCriteria.proto().invoicePaymentBackOut().applyNSF(), nsfCriteria.getValue());
        }
        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord bo, PaymentRecordDTO to) {
        super.enhanceListRetrieved(bo, to);
        Persistence.service().retrieve(to.billingAccount());
        Persistence.service().retrieve(to.billingAccount().lease());
        Persistence.service().retrieve(to.billingAccount().lease().unit().building());
        Persistence.service().retrieve(to.paymentMethod().customer());

        Persistence.ensureRetrieve(bo.invoicePaymentBackOut(), AttachLevel.Attached);
        if (!bo.invoicePaymentBackOut().isNull()) {
            to.rejectedWithNSF().setValue(bo.invoicePaymentBackOut().applyNSF().getValue());
        }
    }
}
