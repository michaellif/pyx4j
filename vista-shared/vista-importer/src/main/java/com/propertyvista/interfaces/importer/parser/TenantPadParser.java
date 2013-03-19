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
package com.propertyvista.interfaces.importer.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
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
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.interfaces.importer.model.PadFileModel;

public class TenantPadParser {

    private final static Logger log = LoggerFactory.getLogger(TenantPadParser.class);

    private static final I18n i18n = I18n.get(TenantPadParser.class);

    private List<PadFileModel> pads = new ArrayList<PadFileModel>();

    public String persistPads(byte[] data, DownloadFormat format) {
        TenantPadCounter counters = new TenantPadCounter();
        pads = parsePads(data, format);
        counters.add(savePads(pads));

        String message = SimpleMessageFormat.format("{0} payment methods created, {1} skipped, {2} amounts updated", counters.imported, counters.skipped,
                counters.updated);
        log.info(message);
        return message;
    }

    private List<PadFileModel> parsePads(byte[] data, DownloadFormat format) {
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
            pads.addAll(receiver.getEntities());
        }
        return pads;

    }

    private TenantPadCounter savePads(List<PadFileModel> entities) {
        TenantPadCounter counters = new TenantPadCounter();
        padFileModel: for (PadFileModel entity : entities) {
            LeaseTermTenant leaseTermTenant = EntityFactory.create(LeaseTermTenant.class);
            {
                EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
                criteria.eq(criteria.proto().leaseParticipant().participantId(), entity.tenantId());
                for (LeaseTermTenant participant : Persistence.service().query(criteria)) {
                    if (participant.leaseParticipant().isInstanceOf(Tenant.class)) {
                        leaseTermTenant = participant;
                    }
                }
            }

            EcheckInfo details = EntityFactory.create(EcheckInfo.class);
            details.accountNo().newNumber().setValue(entity.accountNumber().getValue());
            details.bankId().setValue(entity.bankId().getValue());
            details.branchTransitNumber().setValue(entity.transitNumber().getValue());
            details.nameOn().setValue(entity.name().getValue());

            Persistence.service().retrieve(leaseTermTenant.leaseParticipant().customer());
            Persistence.service().retrieveMember(leaseTermTenant.leaseParticipant().customer().paymentMethods());
            LeasePaymentMethod existingPaymentMethod = retrievePaymentMethod(leaseTermTenant.leaseParticipant().customer(), details);
            if (existingPaymentMethod != null) {
                List<PreauthorizedPayment> payments = new ArrayList<PreauthorizedPayment>();
                {
                    EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
                    criteria.eq(criteria.proto().paymentMethod(), existingPaymentMethod);
                    criteria.eq(criteria.proto().tenant(), leaseTermTenant.leaseParticipant());
                    payments = Persistence.service().query(criteria);
                }
                for (PreauthorizedPayment payment : payments) {
                    if (!payment.isDeleted().getValue()) {
                        if (payment.amount().getValue().equals(new BigDecimal(entity.charge().getValue()))) {
                            counters.skipped++;
                            continue padFileModel;
                        }
                        payment.isDeleted().setValue(Boolean.TRUE);
                        Persistence.service().persist(payment);
                    }
                }
                Persistence.service().persist(createPayment(existingPaymentMethod, entity, leaseTermTenant));
                Persistence.service().commit();
                counters.updated++;
                continue;
            }

            LeasePaymentMethod method = EntityFactory.create(LeasePaymentMethod.class);
            method.isPreauthorized().setValue(Boolean.FALSE);
            method.isOneTimePayment().setValue(Boolean.FALSE);
            method.sameAsCurrent().setValue(Boolean.TRUE);
            method.type().setValue(PaymentType.Echeck);
            method.details().set(details);
            method.customer().set(leaseTermTenant.leaseParticipant().customer());
            method.billingAddress().set(getAddress(leaseTermTenant));

            Persistence.service().retrieve(leaseTermTenant.leaseParticipant().lease());
            Persistence.service().retrieve(leaseTermTenant.leaseParticipant().lease().unit());
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(leaseTermTenant.leaseParticipant().lease().unit().building(), method);

            if (!entity.charge().getValue().isEmpty()) {
                Persistence.service().persist(createPayment(method, entity, leaseTermTenant));
            }
            Persistence.service().commit();
            counters.imported++;
        }

        return counters;
    }

    private PreauthorizedPayment createPayment(LeasePaymentMethod method, PadFileModel entity, LeaseTermTenant leaseTermTenant) {
        PreauthorizedPayment payment = EntityFactory.create(PreauthorizedPayment.class);
        payment.paymentMethod().set(method);
        BigDecimal amount = new BigDecimal(entity.charge().getValue());
        payment.amount().setValue(amount);
        payment.tenant().set(leaseTermTenant.leaseParticipant());
        return payment;
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

    private LeasePaymentMethod retrievePaymentMethod(Customer customer, EcheckInfo newInfo) {
        for (LeasePaymentMethod method : customer.paymentMethods()) {
            if (method.type().getValue().equals(PaymentType.Echeck)) {
                EcheckInfo info = method.details().duplicate(EcheckInfo.class);
                if (info.bankId().getValue().equals(newInfo.bankId().getValue())
                        && info.branchTransitNumber().getValue().equals(newInfo.branchTransitNumber().getValue())
                        && info.accountNo().number().getValue().equals(newInfo.accountNo().newNumber().getValue())) {
                    return method;
                }
            }
        }
        return null;
    }

    private AddressStructured getAddress(LeaseTermTenant leaseTermTenant) {
        Persistence.service().retrieve(leaseTermTenant.leaseTermV());
        Persistence.service().retrieve(leaseTermTenant.leaseTermV().holder().lease());
        Persistence.service().retrieve(leaseTermTenant.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(leaseTermTenant.leaseTermV().holder().lease().unit().building());

        AddressStructured address = leaseTermTenant.leaseTermV().holder().lease().unit().building().info().address().duplicate();
        address.suiteNumber().set(leaseTermTenant.leaseTermV().holder().lease().unit().info().number());

        return address;
    }

    public class TenantPadCounter {

        public int imported;

        public int skipped;

        public int updated;

        public TenantPadCounter() {
            this.imported = 0;
            this.skipped = 0;
            this.updated = 0;
        }

        public void add(TenantPadCounter counters) {
            this.imported += counters.imported;
            this.skipped += counters.skipped;
            this.updated += counters.updated;
        }
    }
}
