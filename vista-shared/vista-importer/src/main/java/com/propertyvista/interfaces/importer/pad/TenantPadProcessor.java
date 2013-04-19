/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.pad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.server.common.util.AddressRetriever;

public class TenantPadProcessor {

    private final static Logger log = LoggerFactory.getLogger(TenantPadProcessor.class);

    private static final I18n i18n = I18n.get(TenantPadProcessor.class);

    static class TenantPadCounter {

        public int notFound;

        public int imported;

        public int unchanged;

        public int updated;

        public int invalid;

        public TenantPadCounter() {
            this.notFound = 0;
            this.imported = 0;
            this.unchanged = 0;
            this.updated = 0;
            this.invalid = 0;
        }

        public void add(TenantPadCounter counters) {
            this.imported += counters.imported;
            this.unchanged += counters.unchanged;
            this.updated += counters.updated;
            this.notFound += counters.notFound;
            this.invalid += counters.invalid;
        }
    }

    public String process(List<PadFileModel> model) {
        TenantPadCounter counters = new TenantPadCounter();
        Map<Lease, List<PadFileModel>> mappedByLease = findLeases(model, counters);

        for (Map.Entry<Lease, List<PadFileModel>> me : mappedByLease.entrySet()) {
            processLeasePads(me.getKey(), me.getValue(), counters);
        }

        String message = SimpleMessageFormat.format("{0} payment methods created, {1} unchanged, {2} amounts updated", counters.imported, counters.unchanged,
                counters.updated);
        if (counters.invalid != 0 || counters.notFound != 0) {
            message += SimpleMessageFormat.format("\n{0} invalid records, {1} tenants not found", counters.invalid, counters.notFound);
        }
        log.info(message);
        return message;
    }

    /**
     * Find corresponding Tenants and Leases
     */
    private Map<Lease, List<PadFileModel>> findLeases(List<PadFileModel> entities, TenantPadCounter counters) {
        Map<Lease, List<PadFileModel>> mappedByLease = new LinkedHashMap<Lease, List<PadFileModel>>();
        for (PadFileModel padFileModel : entities) {
            String tenantId = padFileModel.tenantId().getValue().trim();
            EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.eq(criteria.proto().participantId(), tenantId);
            padFileModel._processorInformation().tenant().set(Persistence.service().retrieve(criteria));

            if (padFileModel._processorInformation().isNull()) {
                padFileModel._import().message().setValue(i18n.tr("Tenant Id ''{0}'' not found in database", tenantId));
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                counters.notFound++;
            } else {
                List<PadFileModel> leaseEntities = mappedByLease.get(padFileModel._processorInformation().tenant().lease());
                if (leaseEntities == null) {
                    leaseEntities = new ArrayList<PadFileModel>();
                    mappedByLease.put(padFileModel._processorInformation().tenant().lease(), leaseEntities);
                }
                leaseEntities.add(padFileModel);
            }
        }
        return mappedByLease;
    }

    /**
     * TODO Load existing LeasePaymentMethods.
     * Calculate proper percentage rounding
     */
    private void processLeasePads(Lease lease, List<PadFileModel> leasePadEntities, TenantPadCounter counters) {
        if (!validateLeasePads(leasePadEntities, counters)) {
            return;
        }
        calulateLeasePercents(leasePadEntities);

        for (final PadFileModel padFileModel : leasePadEntities) {
            TenantPadCounter saveCounter;
            try {
                saveCounter = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<TenantPadCounter, RuntimeException>() {

                    @Override
                    public TenantPadCounter execute() throws RuntimeException {
                        return savePad(padFileModel);
                    }
                });
                counters.add(saveCounter);
            } catch (Throwable e) {
                log.error("pad save error", e);
                counters.invalid++;
            }
        }
    }

    private boolean validateLeasePads(List<PadFileModel> leasePadEntities, TenantPadCounter counters) {
        boolean valid = true;
        for (PadFileModel padFileModel : leasePadEntities) {
            String message = validationMessagePadModel(padFileModel);
            if (message != null) {
                padFileModel._import().message().setValue(message);
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                valid = false;
            }
        }
        if (!valid) {
            counters.invalid += leasePadEntities.size();
            for (PadFileModel padFileModel : leasePadEntities) {
                if (!padFileModel._import().invalid().getValue(Boolean.FALSE)) {
                    padFileModel._import().invalid().setValue(Boolean.TRUE);
                    padFileModel._import().message().setValue(i18n.tr("Other Pads on this lease are invalid"));
                }
            }
        }
        return valid;
    }

    private String validationMessagePadModel(PadFileModel padFileModel) {
        if (padFileModel.accountNumber().isNull()) {
            return i18n.tr("Account Number is required");
        }
        if (!ValidationUtils.isAccountNumberValid(padFileModel.accountNumber().getValue())) {
            return i18n.tr("Account Number should consist of up to 12 digits");
        }
        if (padFileModel.bankId().isNull()) {
            return i18n.tr("Bank Id/Institution is required");
        }
        if (!ValidationUtils.isBankIdNumberValid(padFileModel.accountNumber().getValue())) {
            return i18n.tr("Bank Id/Institution should consist of 3 digits");
        }
        if (padFileModel.transitNumber().isNull()) {
            return i18n.tr("Transit Number is required");
        }
        if (!ValidationUtils.isBranchTransitNumberValid(padFileModel.transitNumber().getValue())) {
            return i18n.tr("Transit Number should consist of 5 digits");
        }

        // We support Upload account only
//        if ((padFileModel.charge().isNull()) && (padFileModel.percent().isNull())) {
//            return i18n.tr("Charge or percent is required");
//        }
        if ((!padFileModel.charge().isNull()) && (!padFileModel.percent().isNull())) {
            return i18n.tr("Charge and percent not supported simultaneously");
        }

        if (!padFileModel.charge().isNull() && (!isValidNumber(padFileModel.charge().getValue()))) {
            return i18n.tr("Charge is not valid number");
        }

        if (!padFileModel.percent().isNull() && (!isValidNumber(padFileModel.percent().getValue()))) {
            return i18n.tr("Percent is not valid number");
        }
        return null;
    }

    private boolean isValidNumber(String value) {
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static void calulateLeasePercents(List<PadFileModel> leasePadEntities) {
        // Merge PaymentMethods

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal percentRoundTotal = BigDecimal.ZERO;
        PadFileModel recordLargest = null;

        for (PadFileModel padFileModel : leasePadEntities) {
            if (!padFileModel.percent().isNull()) {
                BigDecimal percent = new BigDecimal(padFileModel.percent().getValue()).divide(new BigDecimal(100));
                BigDecimal percentRound = percent.setScale(4, BigDecimal.ROUND_HALF_UP);
                padFileModel._processorInformation().percent().setValue(percentRound);
                percentTotal = percentTotal.add(percent);
                percentRoundTotal = percentRoundTotal.add(percentRound);
                if ((recordLargest == null) || (percent.compareTo(recordLargest._processorInformation().percent().getValue()) > 0)) {
                    recordLargest = padFileModel;
                }
            }
        }

        // Percent rounding case of total 100% (+-.01%)  e.g. 33.3% + 66.6%  
        // Make the Largest to pay fractions
        if ((percentTotal.compareTo(percentRoundTotal) != 0) && (percentTotal.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) < 1)) {
            BigDecimal unapidPercentBalance = percentRoundTotal.subtract(BigDecimal.ONE);
            recordLargest._processorInformation().percent().setValue(recordLargest._processorInformation().percent().getValue().add(unapidPercentBalance));
            recordLargest._import().message().setValue("Percent value rounded reaminder added to this record");
        }
    }

    private TenantPadCounter savePad(PadFileModel padFileModel) {
        TenantPadCounter counters = new TenantPadCounter();

        Tenant tenant = padFileModel._processorInformation().tenant();

        EcheckInfo details = EntityFactory.create(EcheckInfo.class);
        details.accountNo().newNumber().setValue(padFileModel.accountNumber().getValue().trim());
        details.bankId().setValue(padFileModel.bankId().getValue().trim());
        details.branchTransitNumber().setValue(padFileModel.transitNumber().getValue().trim());

        if (!padFileModel.name().isNull()) {
            details.nameOn().setValue(padFileModel.name().getValue().trim());
        } else {
            details.nameOn().setValue(tenant.customer().person().name().getStringView());
        }

        Persistence.service().retrieve(tenant.customer());
        Persistence.service().retrieveMember(tenant.customer().paymentMethods());
        LeasePaymentMethod existingPaymentMethod = retrievePaymentMethod(tenant.customer(), details);
        if (existingPaymentMethod != null) {
            // Update PAP if one exists for existing PaymentMethod
            if (!padFileModel.charge().isNull() || !padFileModel.percent().isNull()) {
                List<PreauthorizedPayment> paps = new ArrayList<PreauthorizedPayment>();
                {
                    EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
                    criteria.eq(criteria.proto().paymentMethod(), existingPaymentMethod);
                    criteria.eq(criteria.proto().tenant(), tenant);
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
                    Persistence.service().persist(createPAP(existingPaymentMethod, padFileModel, tenant));
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
            method.customer().set(tenant.customer());
            method.billingAddress().set(AddressRetriever.getLeaseAddress(tenant.lease()));

            Persistence.service().retrieve(tenant.lease());
            Persistence.service().retrieve(tenant.lease().unit());
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(method, tenant.lease().unit().building());

            if (!padFileModel.charge().isNull() || !padFileModel.percent().isNull()) {
                Persistence.service().persist(createPAP(method, padFileModel, tenant));
            }
            counters.imported++;
        }
        return counters;
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

    private boolean isSameAmount(PreauthorizedPayment pap, PadFileModel padFileModel) {
        if (!padFileModel._processorInformation().percent().isNull()) {
            if (pap.amountType().getValue() != AmountType.Percent) {
                return false;
            }
            return pap.percent().getValue().compareTo(padFileModel._processorInformation().percent().getValue()) == 0;
        } else if (!padFileModel.charge().isNull()) {
            if (pap.amountType().getValue() != AmountType.Value) {
                return false;
            }
            BigDecimal amount = new BigDecimal(padFileModel.charge().getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            return pap.value().getValue().compareTo(amount) == 0;
        } else {
            return false;
        }
    }

    private PreauthorizedPayment createPAP(LeasePaymentMethod method, PadFileModel padFileModel, Tenant tenant) {
        PreauthorizedPayment pap = EntityFactory.create(PreauthorizedPayment.class);
        pap.paymentMethod().set(method);

        if (!padFileModel._processorInformation().percent().isNull()) {
            pap.amountType().setValue(AmountType.Percent);
            pap.percent().setValue(padFileModel._processorInformation().percent().getValue());
        } else if (!padFileModel.charge().isNull()) {
            pap.amountType().setValue(AmountType.Value);
            BigDecimal amount = new BigDecimal(padFileModel.charge().getValue());
            pap.value().setValue(amount);
        } else {
            throw new IllegalArgumentException();
        }
        pap.tenant().set(tenant);
        return pap;
    }

}
