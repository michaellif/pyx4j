/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.parser.ImportParser;

public class TenantPadParser implements ImportParser {

    private final static Logger log = LoggerFactory.getLogger(TenantPadParser.class);

    private static final I18n i18n = I18n.get(TenantPadParser.class);

    @Override
    public ImportIO parse(byte[] data, DownloadFormat format) {
        if ((format != DownloadFormat.XLS) && (format != DownloadFormat.XLSX)) {
            throw new IllegalArgumentException();
        }
        XLSLoad loader;
        try {
            loader = new XLSLoad(new ByteArrayInputStream(data), format == DownloadFormat.XLSX);
        } catch (IOException e) {
            log.error("XLSLoad error", e);
            throw new UserRuntimeException(i18n.tr("Unable to read Excel File, {0}", e.getMessage()));
        }
        int sheets = loader.getNumberOfSheets();

        for (int sheetNumber = 0; sheetNumber < sheets; sheetNumber++) {
            if (loader.isSheetHidden(sheetNumber)) {
                continue;
            }
            EntityCSVReciver<PadFileModel> receiver = new PadFileCSVReciver(loader.getSheetName(sheetNumber));
            try {
                if (!loader.loadSheet(sheetNumber, receiver)) {
                    new UserRuntimeException(i18n.tr("Column header declaration not found"));
                }
            } catch (UserRuntimeException e) {
                log.error("XLSLoad error", e);
                throw new UserRuntimeException(i18n.tr("{0} on sheet ''{1}''", e.getMessage(), loader.getSheetName(sheetNumber)));
            }
            convertUnits(receiver.getEntities());
        }
        return null;
    }

    private void convertUnits(List<PadFileModel> entities) {
        for (PadFileModel entity : entities) {
            LeaseTermParticipant<?> leaseTermParticipant = EntityFactory.create(LeaseTermParticipant.class);
            {
                @SuppressWarnings("rawtypes")
                EntityQueryCriteria<LeaseTermParticipant> criteria = EntityQueryCriteria.create(LeaseTermParticipant.class);
                criteria.eq(criteria.proto().leaseParticipant().participantId(), entity.tenantId());
                leaseTermParticipant = Persistence.service().query(criteria).get(0);
            }

            EcheckInfo details = EntityFactory.create(EcheckInfo.class);
            details.accountNo().newNumber().setValue(entity.accountNumber().getValue());
            details.bankId().setValue(entity.bankId().getValue());
            details.branchTransitNumber().setValue(entity.transitNumber().getValue());
            details.nameOn().setValue(entity.name().getValue());

            Persistence.service().retrieve(leaseTermParticipant.leaseParticipant().customer());
            Persistence.service().retrieveMember(leaseTermParticipant.leaseParticipant().customer().paymentMethods());
            if (padExists(leaseTermParticipant.leaseParticipant().customer(), details)) {
                continue;
            }

            LeasePaymentMethod method = EntityFactory.create(LeasePaymentMethod.class);
            method.isPreauthorized().setValue(Boolean.FALSE);
            method.isOneTimePayment().setValue(Boolean.FALSE);
            method.sameAsCurrent().setValue(Boolean.TRUE);
            method.type().setValue(PaymentType.Echeck);
            method.details().set(details);
            method.customer().set(leaseTermParticipant.leaseParticipant().customer());

            method.billingAddress().set(getAddress(leaseTermParticipant));

            Persistence.service().retrieve(leaseTermParticipant.leaseParticipant().lease());
            Persistence.service().retrieve(leaseTermParticipant.leaseParticipant().lease().unit());

            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(leaseTermParticipant.leaseParticipant().lease().unit().building(),
                    method);
            Persistence.service().commit();
        }
    }

    private static class PadFileCSVReciver extends EntityCSVReciver<PadFileModel> {
        String sheetNumber;

        public PadFileCSVReciver(String sheetName) {
            super(PadFileModel.class);
            this.sheetNumber = sheetName;
            this.setMemberNamesAsHeaders(false);
            this.setHeaderLinesCount(1, 2);
            this.setHeadersMatchMinimum(3);
            this.setVerifyRequiredHeaders(true);
            this.setVerifyRequiredValues(true);
        }

        @Override
        public void onRow(PadFileModel entity) {
            if (!entity.isNull()) {
                entity._import().row().setValue(getCurrentRow());
                entity._import().sheet().setValue(sheetNumber);
                super.onRow(entity);
            }
        }

    }

    private boolean padExists(Customer customer, EcheckInfo newInfo) {
        for (LeasePaymentMethod method : customer.paymentMethods()) {
            if (method.type().getValue().equals(PaymentType.Echeck)) {
                EcheckInfo info = method.details().duplicate(EcheckInfo.class);
                if (info.bankId().getValue().equals(newInfo.bankId().getValue())
                        && info.branchTransitNumber().getValue().equals(newInfo.branchTransitNumber().getValue())
                        && info.accountNo().number().getValue().equals(newInfo.accountNo().newNumber().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private AddressStructured getAddress(LeaseTermParticipant<?> leaseTermParticipant) {
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV());
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV().holder().lease());
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV().holder().lease().unit().building());

        AddressStructured address = leaseTermParticipant.leaseTermV().holder().lease().unit().building().info().address().duplicate();
        address.suiteNumber().set(leaseTermParticipant.leaseTermV().holder().lease().unit().info().number());

        return address;
    }
}
