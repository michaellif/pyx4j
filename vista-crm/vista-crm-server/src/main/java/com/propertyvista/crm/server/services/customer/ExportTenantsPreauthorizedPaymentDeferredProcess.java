/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 1, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;
import com.propertyvista.interfaces.importer.pad.PadFileExportModel;

@SuppressWarnings("serial")
public class ExportTenantsPreauthorizedPaymentDeferredProcess extends AbstractDeferredProcess {

    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    @Override
    public void execute() {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
        EntityReportFormatter<PadFileExportModel> entityFormatter = new EntityReportFormatter<PadFileExportModel>(PadFileExportModel.class);
        entityFormatter.createHeader(formatter);

        try {
            Persistence.service().startBackgroundProcessTransaction();

            final EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
            criteria.asc(criteria.proto().tenant().lease().unit().building().propertyCode());
            criteria.asc(criteria.proto().tenant().lease().leaseId());
            maximum = Persistence.service().count(criteria);

            ICursorIterator<PreauthorizedPayment> tenants = Persistence.service().query(null, criteria, AttachLevel.Attached);
            try {
                while (tenants.hasNext()) {
                    PreauthorizedPayment preauthorizedPayment = tenants.next();
                    Persistence.service().retrieveMember(preauthorizedPayment.tenant());
                    Persistence.service().retrieveMember(preauthorizedPayment.tenant().lease());
                    Persistence.service().retrieveMember(preauthorizedPayment.tenant().lease().unit().building());
                    formatPreauthorizedPayment(formatter, entityFormatter, preauthorizedPayment);
                    ++progress;
                }
            } finally {
                tenants.close();
            }

        } finally {
            Persistence.service().endTransaction();
        }

        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        fileName = VistaDeployment.getCurrentPmc().name().getValue() + "-PreauthorizedPayments.xlsx";
        d.save(fileName);
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            DeferredProcessProgressResponse r = super.status();
            r.setProgress(progress);
            r.setProgressMaximum(maximum);
            return r;
        }
    }

    private void formatPreauthorizedPayment(ReportTableFormatter formatter, EntityReportFormatter<PadFileExportModel> entityFormatter,
            PreauthorizedPayment preauthorizedPayment) {

        for (PreauthorizedPaymentCoveredItem item : preauthorizedPayment.coveredItems()) {
            PadFileExportModel model = EntityFactory.create(PadFileExportModel.class);

            model.charge().setValue(item.amount().getStringView());

            model.property().setValue(preauthorizedPayment.tenant().lease().unit().building().propertyCode().getValue());
            model.leaseId().setValue(preauthorizedPayment.tenant().lease().leaseId().getValue());
            model.leaseStatus().setValue(preauthorizedPayment.tenant().lease().status().getValue());
            model.expectedMoveOut().setValue(preauthorizedPayment.tenant().lease().expectedMoveOut().getValue());

            model.unit().setValue(preauthorizedPayment.tenant().lease().unit().info().number().getStringView());
            model.tenantId().setValue(preauthorizedPayment.tenant().participantId().getValue());
            model.papApplicable().setValue(true);
            model.recurringEFT().setValue(true);

            if (preauthorizedPayment.paymentMethod().type().getValue().equals(PaymentType.Echeck)) {
                EcheckInfo echeckInfo = preauthorizedPayment.paymentMethod().details().duplicate(EcheckInfo.class);

                model.name().setValue(echeckInfo.nameOn().getValue());
                model.bankId().setValue(echeckInfo.bankId().getValue());
                model.transitNumber().setValue(echeckInfo.branchTransitNumber().getValue());
                model.accountNumber().setValue(echeckInfo.accountNo().number().getValue());
            }

            model.estimatedCharge().setValue(item.billableItem().agreedPrice().getStringView());
            model.chargeId().setValue(item.billableItem().id().getStringView());

            if (item.billableItem().extraData().isInstanceOf(YardiLeaseChargeData.class)) {
                model.chargeCode().setValue(item.billableItem().extraData().duplicate(YardiLeaseChargeData.class).chargeCode().getValue());
            }

            entityFormatter.reportEntity(formatter, model);
        }
    }
}
