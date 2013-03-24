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
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;
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

        String message = SimpleMessageFormat.format("{0} payment methods created, {1} unchanged, {2} amounts updated", counters.imported, counters.unchanged,
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
        padFileModel: for (PadFileModel padFileModel : entities) {
            String tenantId = padFileModel.tenantId().getValue().trim();
            LeaseTermTenant leaseTermTenant = EntityFactory.create(LeaseTermTenant.class);
            {
                EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
                criteria.eq(criteria.proto().leaseParticipant().participantId(), tenantId);
                boolean isFound = false;
                for (LeaseTermTenant participant : Persistence.service().query(criteria)) {
                    if (participant.leaseParticipant().isInstanceOf(Tenant.class)) {
                        leaseTermTenant = participant;
                        isFound = true;
                    }
                }
                if (!isFound) {
                    throw new UserRuntimeException(i18n.tr("Tenant Id ''{0}'' not found in database, row {1}", tenantId, padFileModel._import().row()
                            .getValue()));
                }
            }

            EcheckInfo details = EntityFactory.create(EcheckInfo.class);
            details.accountNo().newNumber().setValue(padFileModel.accountNumber().getValue().trim());
            details.bankId().setValue(padFileModel.bankId().getValue().trim());
            details.branchTransitNumber().setValue(padFileModel.transitNumber().getValue().trim());
            details.nameOn().setValue(padFileModel.name().getValue().trim());

            Persistence.service().retrieve(leaseTermTenant.leaseParticipant().customer());
            Persistence.service().retrieveMember(leaseTermTenant.leaseParticipant().customer().paymentMethods());
            LeasePaymentMethod existingPaymentMethod = retrievePaymentMethod(leaseTermTenant.leaseParticipant().customer(), details);
            if (existingPaymentMethod != null) {
                // Update PAP if one exists for existing PaymentMethod
                if (!padFileModel.charge().isNull() || !padFileModel.percent().isNull()) {
                    List<PreauthorizedPayment> paps = new ArrayList<PreauthorizedPayment>();
                    {
                        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
                        criteria.eq(criteria.proto().paymentMethod(), existingPaymentMethod);
                        criteria.eq(criteria.proto().tenant(), leaseTermTenant.leaseParticipant());
                        paps = Persistence.service().query(criteria);
                    }
                    //We do not support import of more then one PAP per PaymentMethod
                    boolean sameAmountPapFound = false;
                    for (PreauthorizedPayment pap : paps) {
                        if (!pap.isDeleted().getValue()) {
                            if (!isSameAmount(pap, padFileModel)) {
                                pap.isDeleted().setValue(Boolean.TRUE);
                                Persistence.service().persist(pap);
                            } else {
                                sameAmountPapFound = true;
                            }
                        }
                    }
                    if (!sameAmountPapFound) {
                        Persistence.service().persist(createPAP(existingPaymentMethod, padFileModel, leaseTermTenant));
                        Persistence.service().commit();
                        counters.updated++;
                    } else {
                        counters.unchanged++;
                    }
                } else {
                    counters.unchanged++;
                }
            } else {

                LeasePaymentMethod method = EntityFactory.create(LeasePaymentMethod.class);
                method.isProfiledMethod().setValue(Boolean.TRUE);
                method.sameAsCurrent().setValue(Boolean.TRUE);
                method.type().setValue(PaymentType.Echeck);
                method.details().set(details);
                method.customer().set(leaseTermTenant.leaseParticipant().customer());
                method.billingAddress().set(getAddress(leaseTermTenant));

                Persistence.service().retrieve(leaseTermTenant.leaseParticipant().lease());
                Persistence.service().retrieve(leaseTermTenant.leaseParticipant().lease().unit());
                ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(method,
                        leaseTermTenant.leaseParticipant().lease().unit().building());

                if (!padFileModel.charge().isNull() || !padFileModel.percent().isNull()) {
                    Persistence.service().persist(createPAP(method, padFileModel, leaseTermTenant));
                }
                Persistence.service().commit();
                counters.imported++;
            }
        }

        return counters;
    }

    private boolean isSameAmount(PreauthorizedPayment pap, PadFileModel padFileModel) {
        if (!padFileModel.charge().isNull()) {
            if (pap.amountType().getValue() != AmountType.Value) {
                return false;
            }
            BigDecimal amount = new BigDecimal(padFileModel.charge().getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            return pap.amount().getValue().compareTo(amount) == 0;
        } else if (!padFileModel.percent().isNull()) {
            if (pap.amountType().getValue() != AmountType.Percent) {
                return false;
            }
            BigDecimal percent = new BigDecimal(padFileModel.percent().getValue()).divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP);
            return pap.amount().getValue().compareTo(percent) == 0;
        } else {
            return false;
        }
    }

    private PreauthorizedPayment createPAP(LeasePaymentMethod method, PadFileModel padFileModel, LeaseTermTenant leaseTermTenant) {
        PreauthorizedPayment pap = EntityFactory.create(PreauthorizedPayment.class);
        pap.paymentMethod().set(method);

        if (!padFileModel.charge().isNull()) {
            pap.amountType().setValue(AmountType.Value);
            BigDecimal amount = new BigDecimal(padFileModel.charge().getValue());
            pap.amount().setValue(amount);
        } else if (!padFileModel.percent().isNull()) {
            pap.amountType().setValue(AmountType.Percent);
            BigDecimal percent = new BigDecimal(padFileModel.percent().getValue()).divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP);
            pap.amount().setValue(percent);
        } else {
            throw new IllegalArgumentException();
        }
        pap.tenant().set(leaseTermTenant.leaseParticipant());
        return pap;
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

        public int unchanged;

        public int updated;

        public TenantPadCounter() {
            this.imported = 0;
            this.unchanged = 0;
            this.updated = 0;
        }

        public void add(TenantPadCounter counters) {
            this.imported += counters.imported;
            this.unchanged += counters.unchanged;
            this.updated += counters.updated;
        }
    }
}
