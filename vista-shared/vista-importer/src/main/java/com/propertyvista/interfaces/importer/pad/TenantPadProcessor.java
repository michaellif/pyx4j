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
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.payment.PaymentBillableUtils;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.model.PadProcessorInformation;
import com.propertyvista.interfaces.importer.model.PadProcessorInformation.PadProcessingStatus;
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

        public int removed;

        public TenantPadCounter() {
            this.notFound = 0;
            this.imported = 0;
            this.unchanged = 0;
            this.updated = 0;
            this.invalid = 0;
            this.removed = 0;
        }

        public void add(TenantPadCounter counters) {
            this.imported += counters.imported;
            this.unchanged += counters.unchanged;
            this.updated += counters.updated;
            this.notFound += counters.notFound;
            this.invalid += counters.invalid;
            this.removed += counters.removed;
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
            message += SimpleMessageFormat.format(", {0} invalid records, {1} tenants not found", counters.invalid, counters.notFound);
        }
        log.info(message);
        return message;
    }

    public String processOfflineTest(List<PadFileModel> model) {
        TenantPadCounter counters = new TenantPadCounter();
        Map<String, List<PadFileModel>> mappedByLease = findLeasesOfflineTest(model, counters);

        for (Map.Entry<String, List<PadFileModel>> me : mappedByLease.entrySet()) {
            List<PadFileModel> leasePadEntities = me.getValue();
            if (validateLeasePads(leasePadEntities, counters)) {
                calulateLeasePercents(leasePadEntities);
            }

            for (PadFileModel padFileModel : leasePadEntities) {
                if (padFileModel._processorInformation().status().isNull()) {
                    counters.imported++;
                }
            }
        }

        String message = SimpleMessageFormat.format("{0} payment methods created, {1} unchanged, {2} amounts updated", counters.imported, counters.unchanged,
                counters.updated);
        if (counters.removed != 0) {
            message += SimpleMessageFormat.format(", {0} removed old pap records", counters.removed);
        }

        if (counters.invalid != 0 || counters.notFound != 0) {
            message += SimpleMessageFormat.format(", {0} invalid records, {1} tenants not found", counters.invalid, counters.notFound);
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
            if (padFileModel.ignore().getValue(false)) {
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.ignoredByRequest);
                continue;
            }

            if (!padFileModel.tenantId().isNull()) {
                String tenantId = padFileModel.tenantId().getValue().trim();
                EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
                criteria.eq(criteria.proto().participantId(), tenantId);
                if (!padFileModel.property().isNull()) {
                    criteria.eq(criteria.proto().lease().unit().building().propertyCode(), padFileModel.property());
                }
                try {
                    padFileModel._processorInformation().tenant().set(Persistence.retrieveUnique(criteria, AttachLevel.Attached));
                } catch (UniqueConstraintUserRuntimeException e) {
                    padFileModel._import().invalid().setValue(Boolean.TRUE);
                    padFileModel._import().message().setValue(e.getMessage());
                    counters.notFound++;
                    continue;
                }
                if (padFileModel._processorInformation().tenant().isNull()) {
                    padFileModel._import().message().setValue(i18n.tr("Tenant Id ''{0}'' not found in database", tenantId));
                }
            } else if (!padFileModel.leaseId().isNull()) {
                String leaseId = padFileModel.leaseId().getValue().trim();
                EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
                criteria.eq(criteria.proto().lease().leaseId(), leaseId);
                criteria.isCurrent(criteria.proto().leaseTermParticipants().$().leaseTermV());
                criteria.eq(criteria.proto().leaseTermParticipants().$().role(), LeaseTermParticipant.Role.Applicant);
                if (!padFileModel.property().isNull()) {
                    criteria.eq(criteria.proto().lease().unit().building().propertyCode(), padFileModel.property());
                }
                try {
                    padFileModel._processorInformation().tenant().set(Persistence.retrieveUnique(criteria, AttachLevel.Attached));
                } catch (UniqueConstraintUserRuntimeException e) {
                    padFileModel._import().invalid().setValue(Boolean.TRUE);
                    padFileModel._import().message().setValue(e.getMessage());
                    counters.notFound++;
                    continue;
                }

                if (padFileModel._processorInformation().tenant().isNull()) {
                    padFileModel._import().message().setValue(i18n.tr("Lease Id ''{0}'' not found in database", leaseId));
                }
            } else {
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                padFileModel._import().message().setValue(i18n.tr("Tenant Id or Lease Id are required"));
                continue;
            }

            if (padFileModel._processorInformation().tenant().isNull()) {
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.notFound);
                counters.notFound++;
                continue;
            }

            Persistence.ensureRetrieve(padFileModel._processorInformation().tenant().lease(), AttachLevel.Attached);
            Persistence.ensureRetrieve(padFileModel._processorInformation().tenant().lease().unit().building(), AttachLevel.Attached);
            if (padFileModel.property().isNull()) {
                padFileModel.property().setValue(padFileModel._processorInformation().tenant().lease().unit().building().propertyCode().getValue());
            } else {
                if (!padFileModel.property().equals(padFileModel._processorInformation().tenant().lease().unit().building().propertyCode())) {
                    padFileModel._import().invalid().setValue(Boolean.TRUE);
                    padFileModel
                            ._import()
                            .message()
                            .setValue(
                                    i18n.tr("Property Code do not match {0}", padFileModel._processorInformation().tenant().lease().unit().building()
                                            .propertyCode()));
                    counters.invalid++;
                    continue;
                }
            }

            List<PadFileModel> leaseEntities = mappedByLease.get(padFileModel._processorInformation().tenant().lease());
            if (leaseEntities == null) {
                leaseEntities = new ArrayList<PadFileModel>();
                mappedByLease.put(padFileModel._processorInformation().tenant().lease(), leaseEntities);
            }
            leaseEntities.add(padFileModel);

        }
        return mappedByLease;
    }

    private Map<String, List<PadFileModel>> findLeasesOfflineTest(List<PadFileModel> entities, TenantPadCounter counters) {
        Map<String, List<PadFileModel>> mappedByLease = new LinkedHashMap<String, List<PadFileModel>>();
        for (PadFileModel padFileModel : entities) {
            if (padFileModel.ignore().getValue(false)) {
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.ignoredByRequest);
                continue;
            }
            String leaseId;
            if (!padFileModel.leaseId().isNull()) {
                leaseId = padFileModel.leaseId().getValue().trim();
            } else {
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                padFileModel._import().message().setValue(i18n.tr("Tenant Id or Lease Id are required"));
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.invalid);
                counters.invalid++;
                continue;
            }

            List<PadFileModel> leaseEntities = mappedByLease.get(leaseId);
            if (leaseEntities == null) {
                leaseEntities = new ArrayList<PadFileModel>();
                mappedByLease.put(leaseId, leaseEntities);
            }
            leaseEntities.add(padFileModel);

        }
        return mappedByLease;
    }

    /**
     * TODO Load existing LeasePaymentMethods.
     * Calculate proper percentage with consideration of rounding
     */
    private void processLeasePads(final Lease lease, List<PadFileModel> leasePadEntities, final TenantPadCounter counters) {
        correctPadParsing(leasePadEntities);
        if (!validateLeasePads(leasePadEntities, counters)) {
            return;
        }
        assignUid(leasePadEntities);
        calulateLeasePercents(leasePadEntities);

        final List<AutopayAgreement> createdOrExistingPaps = new ArrayList<AutopayAgreement>();

        for (final PadFileModel padFileModel : leasePadEntities) {
            if ((!padFileModel._processorInformation().status().isNull())
                    && (padFileModel._processorInformation().status().getValue() != PadProcessingStatus.ignoredUinitializedChargeSplit)) {
                continue;
            }

            final TenantPadCounter saveCounter = new TenantPadCounter();
            try {
                AutopayAgreement pap = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AutopayAgreement, RuntimeException>() {

                    @Override
                    public AutopayAgreement execute() throws RuntimeException {
                        return savePad(padFileModel, saveCounter);
                    }
                });

                counters.add(saveCounter);
                if (pap != null) {
                    createdOrExistingPaps.add(pap);
                }
            } catch (Throwable e) {
                padFileModel._import().invalid().setValue(true);
                padFileModel._import().message().setValue(e.getMessage());
                log.debug("Error with PadFileModel {} ", DataDump.toXmlString(padFileModel));
                log.error("tenant {} pad save error", padFileModel._processorInformation().tenant().participantId().getValue(), e);
                counters.invalid++;
            }
        }

        // Remove other PreauthorizedPayment on this lease
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {

                EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
                criteria.eq(criteria.proto().tenant().lease(), lease);
                criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
                for (AutopayAgreement pap : Persistence.service().query(criteria)) {
                    if (!createdOrExistingPaps.contains(pap)) {
                        ServerSideFactory.create(PaymentMethodFacade.class).deleteAutopayAgreement(pap);
                        counters.removed++;
                    }
                }

                return null;
            }
        });

    }

    private void correctPadParsing(List<PadFileModel> leasePadEntities) {
        for (PadFileModel padFileModel : leasePadEntities) {
            trimValue(padFileModel.chargeCode());
            trimValue(padFileModel.chargeId());

            trimValue(padFileModel.percent());
            trimValue(padFileModel.charge());
            trimValue(padFileModel.estimatedCharge());

            correctDollars(padFileModel.charge());
            correctDollars(padFileModel.estimatedCharge());
        }
    }

    private boolean validateLeasePads(List<PadFileModel> leasePadEntities, TenantPadCounter counters) {
        PadFileModel invalid = null;
        for (PadFileModel padFileModel : leasePadEntities) {
            String message = validationMessagePadModel(padFileModel);
            if (message != null) {
                System.err.println(message);
                padFileModel._import().message().setValue(message);
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.invalid);
                invalid = padFileModel;
            }
        }
        if (invalid != null) {
            counters.invalid += leasePadEntities.size();
            for (PadFileModel padFileModel : leasePadEntities) {
                if (!padFileModel._import().invalid().getValue(Boolean.FALSE)) {
                    padFileModel._import().invalid().setValue(Boolean.TRUE);
                    padFileModel._processorInformation().status().setValue(PadProcessingStatus.anotherRecordInvalid);
                    padFileModel._import().message().setValue(i18n.tr("Other Pad (row {0}) on this lease is invalid", invalid._import().row()));
                }
            }
            return false;
        } else {
            return true;
        }
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
        if (!ValidationUtils.isBankIdNumberValid(padFileModel.bankId().getValue())) {
            return i18n.tr("Bank Id/Institution {0} should consist of 3 digits", padFileModel.bankId());
        }
        if (padFileModel.transitNumber().isNull()) {
            return i18n.tr("Transit Number is required");
        }
        if (!ValidationUtils.isBranchTransitNumberValid(padFileModel.transitNumber().getValue())) {
            return i18n.tr("Transit Number {0} should consist of 5 digits", padFileModel.transitNumber());
        }

        // We support Upload account only
//        if ((padFileModel.charge().isNull()) && (padFileModel.percent().isNull())) {
//            return i18n.tr("Charge or percent is required");
//        }
        // Charge will override percent
//        if ((!padFileModel.charge().isNull()) && (!padFileModel.percent().isNull())) {
//            return i18n.tr("Charge and percent not supported simultaneously");
//        }

        if (!padFileModel.charge().isNull() && (!isValidateAndCorrectAmount(padFileModel.charge()))) {
            return i18n.tr("Charge ''{0}'' is not valid number", padFileModel.charge());
        }

        if ((!padFileModel.percent().isNull()) && (padFileModel.percent().getValue().endsWith("%"))) {
            padFileModel.percent().setValue(padFileModel.percent().getValue().substring(0, padFileModel.percent().getValue().length() - 1));
        }

        if (!padFileModel.percent().isNull() && (!isValidateAndCorrectNumber(padFileModel.percent()))) {
            return i18n.tr("Percent ''{0}'' is not valid number", padFileModel.percent());
        }

        if (!padFileModel.estimatedCharge().isNull() && (!isValidateAndCorrectAmount(padFileModel.estimatedCharge()))) {
            return i18n.tr("Estimated Charge ''{0}'' is not valid number", padFileModel.estimatedCharge());
        }

        return null;
    }

    private void correctDollars(IPrimitive<String> amount) {
        if ((!amount.isNull()) && (amount.getValue().startsWith("$"))) {
            amount.setValue(amount.getValue().substring(1, amount.getValue().length()).trim());
        } else if ((!amount.isNull()) && (amount.getValue().endsWith("$"))) {
            amount.setValue(amount.getValue().substring(amount.getValue().length() - 1).trim());
        }
    }

    private void trimValue(IPrimitive<String> value) {
        if (!value.isNull()) {
            value.setValue(value.getValue().trim());
        }
    }

    private boolean isValidateAndCorrectAmount(IPrimitive<String> value) {
        try {
            Double.parseDouble(value.getValue());
            return true;
        } catch (NumberFormatException e) {
        }
        try {
            Number number = new DecimalFormat("#,##0.00").parse(value.getValue());
            value.setValue(new DecimalFormat("0.00").format(number.doubleValue()));
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidateAndCorrectNumber(IPrimitive<String> value) {
        try {
            Double.parseDouble(value.getValue());
            return true;
        } catch (NumberFormatException e) {
        }
        try {
            Number number = new DecimalFormat("#,##0.0000").parse(value.getValue());
            value.setValue(number.toString());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    static boolean allHaveMember(List<PadFileModel> leasePadEntities, IPrimitive<String> proto) {
        for (PadFileModel padFileModel : leasePadEntities) {
            String v = (String) padFileModel.getMember(proto.getFieldName()).getValue();
            if (!CommonsStringUtils.isStringSet(v)) {
                return false;
            }
        }
        return true;
    }

    static boolean anyHaveMember(List<PadFileModel> leasePadEntities, IPrimitive<Boolean> proto) {
        for (PadFileModel padFileModel : leasePadEntities) {
            Boolean v = (Boolean) padFileModel.getMember(proto.getFieldName()).getValue();
            if (Boolean.TRUE.equals(v)) {
                return true;
            }
        }
        return false;
    }

    private void assignUid(List<PadFileModel> leasePadEntities) {
        Map<String, List<PadFileModel>> mappedByAccount = mapByAccount(leasePadEntities);

        for (Map.Entry<String, List<PadFileModel>> me : mappedByAccount.entrySet()) {

            Map<String, Integer> chargeCodeItemsCount = new HashMap<String, Integer>();
            for (PadFileModel padFileModel : me.getValue()) {
                if (!padFileModel.chargeCode().isNull()) {
                    Integer chargeCodeItemNo = chargeCodeItemsCount.get(padFileModel.chargeCode().getValue());
                    if (chargeCodeItemNo == null) {
                        chargeCodeItemNo = 1;
                    } else {
                        chargeCodeItemNo = chargeCodeItemNo + 1;
                    }
                    chargeCodeItemsCount.put(padFileModel.chargeCode().getValue(), chargeCodeItemNo);
                    padFileModel._processorInformation().billableItemId().setValue(padFileModel.chargeCode().getValue() + ":" + chargeCodeItemNo);
                }
            }

        }
    }

    static void calulateLeasePercents(List<PadFileModel> leasePadEntities) {
        boolean allHaveChargeCode = allHaveMember(leasePadEntities, EntityFactory.getEntityPrototype(PadFileModel.class).chargeCode());
        boolean allHaveEstimatedCharge = allHaveMember(leasePadEntities, EntityFactory.getEntityPrototype(PadFileModel.class).estimatedCharge());

        if (allHaveChargeCode && allHaveEstimatedCharge) {
            // Yardi Migration Mode
            eliminateUninitializedChargeSplitYardi(leasePadEntities);
            BigDecimal estimatedChargeTotal = calulateEstimatedChargesTotal(leasePadEntities);
            // Merge PaymentMethods from the same account
            Map<String, List<PadFileModel>> mappedByAccount = mapByAccount(leasePadEntities);

            for (Map.Entry<String, List<PadFileModel>> me : mappedByAccount.entrySet()) {
                mergeToFirstRow(estimatedChargeTotal, me.getValue());
            }
            calulateSubsetPercentsRounding(leasePadEntities);
        } else {
            // just do Rounding
            calulateSubsetPercentsRounding(leasePadEntities);
        }

    }

    private static class ChargeCodeRecords {

        BigDecimal estimatedCharge;

        List<PadFileModel> entities = new ArrayList<PadFileModel>();

        BigDecimal entitiesTotal;

        int uninitializedCount = 0;

    }

    static void eliminateUninitializedChargeSplitYardi(List<PadFileModel> leasePadEntities) {
        Map<String, ChargeCodeRecords> recordsByChargeCode = new HashMap<String, ChargeCodeRecords>();
        for (PadFileModel padFileModel : leasePadEntities) {
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            if (!isPapApplicable(padFileModel)) {
                continue;
            }
            if (padFileModel.charge().isNull()) {
                BigDecimal estimatedCharge = new BigDecimal(padFileModel.estimatedCharge().getValue());

                ChargeCodeRecords chargeCodeRecords = recordsByChargeCode.get(uniqueChargeCode(padFileModel));
                if (chargeCodeRecords == null) {
                    chargeCodeRecords = new ChargeCodeRecords();
                    chargeCodeRecords.entities.add(padFileModel);
                    chargeCodeRecords.estimatedCharge = estimatedCharge;
                    recordsByChargeCode.put(uniqueChargeCode(padFileModel), chargeCodeRecords);
                } else {
                    if (chargeCodeRecords.estimatedCharge.compareTo(estimatedCharge) != 0) {
                        padFileModel._import().message().setValue(i18n.tr("estimatedCharge for Charge {0} are changing", padFileModel.chargeCode()));
                        padFileModel._import().invalid().setValue(Boolean.TRUE);
                        padFileModel._processorInformation().status().setValue(PadProcessingStatus.invalid);
                        continue;
                    }
                    chargeCodeRecords.entities.add(padFileModel);
                }
                if (padFileModel.percent().isNull()) {
                    chargeCodeRecords.uninitializedCount++;
                }
            }
        }

        // This is done because of the complexity in creation of extract from yardi 
        // Eliminate uninitialized charge split in Yardi
        for (ChargeCodeRecords chargeCodeRecords : recordsByChargeCode.values()) {
            // All uninitialized
            if (chargeCodeRecords.entities.size() == chargeCodeRecords.uninitializedCount) {
                // Fist is considered by Yardi as 100%
                PadFileModel firstRecord = chargeCodeRecords.entities.get(0);
                for (PadFileModel padFileModel : chargeCodeRecords.entities) {
                    if (padFileModel != firstRecord) {
                        padFileModel._import().message()
                                .setValue(i18n.tr("Ignored as uninitialized ChargeSplit; used only record {0}", firstRecord._import().row()));
                        padFileModel._processorInformation().status().setValue(PadProcessingStatus.ignoredUinitializedChargeSplit);
                    }
                }
            } else
            // There are some Initialized records
            if (chargeCodeRecords.uninitializedCount > 0) {
                for (PadFileModel padFileModel : chargeCodeRecords.entities) {
                    if (padFileModel.percent().isNull()) {
                        padFileModel._import().message().setValue(i18n.tr("Ignored as uninitialized ChargeSplit"));
                        padFileModel._processorInformation().status().setValue(PadProcessingStatus.ignoredUinitializedChargeSplit);
                    }
                }
            }
        }
    }

    private static String uniqueChargeCode(PadFileModel padFileModel) {
        return padFileModel.chargeCode().getValue() + "$" + padFileModel.chargeId().getValue();
    }

    static BigDecimal calulateEstimatedChargesTotal(List<PadFileModel> leasePadEntities) {
        Map<String, ChargeCodeRecords> recordsByChargeCode = new HashMap<String, ChargeCodeRecords>();
        BigDecimal estimatedChargeTotal = BigDecimal.ZERO;
        for (PadFileModel padFileModel : leasePadEntities) {
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            BigDecimal estimatedChargeSplit;
            if (padFileModel.charge().isNull()) {
                BigDecimal charge = DomainUtil.roundMoney(new BigDecimal(padFileModel.estimatedCharge().getValue()));
                padFileModel._processorInformation().chargeAmount().setValue(charge);
                BigDecimal estimatedCharge = charge;
                if (!padFileModel.percent().isNull()) {
                    BigDecimal percent = new BigDecimal(padFileModel.percent().getValue());
                    // This is the way yardi does this
                    estimatedChargeSplit = estimatedCharge.multiply(percent).divide(new BigDecimal(100));
                } else {
                    // 100% assumed
                    estimatedChargeSplit = estimatedCharge;
                }
                padFileModel._processorInformation().chargeEftAmount().setValue(estimatedChargeSplit.setScale(2, RoundingMode.HALF_DOWN));
                // Count each chargeCode once.
                ChargeCodeRecords chargeCodeRecords = recordsByChargeCode.get(uniqueChargeCode(padFileModel));
                if (chargeCodeRecords == null) {
                    estimatedChargeTotal = estimatedChargeTotal.add(estimatedCharge);
                    chargeCodeRecords = new ChargeCodeRecords();
                    chargeCodeRecords.estimatedCharge = charge;
                    chargeCodeRecords.entities.add(padFileModel);
                    chargeCodeRecords.entitiesTotal = padFileModel._processorInformation().chargeEftAmount().getValue();
                    recordsByChargeCode.put(uniqueChargeCode(padFileModel), chargeCodeRecords);
                } else {
                    chargeCodeRecords.entities.add(padFileModel);
                    chargeCodeRecords.entitiesTotal = chargeCodeRecords.entitiesTotal.add(padFileModel._processorInformation().chargeEftAmount().getValue());
                }
            } else {
                BigDecimal charge = DomainUtil.roundMoney(new BigDecimal(padFileModel.charge().getValue()));
                padFileModel._processorInformation().chargeEftAmount().setValue(charge);

                if (padFileModel.estimatedCharge().isNull()) {
                    padFileModel._processorInformation().chargeAmount().setValue(charge);
                } else {
                    BigDecimal chargeEst = DomainUtil.roundMoney(new BigDecimal(padFileModel.estimatedCharge().getValue()));
                    padFileModel._processorInformation().chargeAmount().setValue(chargeEst);
                }

                estimatedChargeSplit = charge;
                estimatedChargeTotal = estimatedChargeTotal.add(estimatedChargeSplit);
            }

            padFileModel._processorInformation().estimatedChargeSplit().setValue(estimatedChargeSplit);
        }

        // 1c moved to last account
        for (ChargeCodeRecords chargeCodeRecords : recordsByChargeCode.values()) {
            if (chargeCodeRecords.estimatedCharge.subtract(chargeCodeRecords.entitiesTotal).abs().compareTo(new BigDecimal("0.02")) <= 0) {
                BigDecimal delta = chargeCodeRecords.estimatedCharge.subtract(chargeCodeRecords.entitiesTotal);
                PadFileModel lastRecord = chargeCodeRecords.entities.get(chargeCodeRecords.entities.size() - 1);
                lastRecord._processorInformation().chargeEftAmount().setValue(lastRecord._processorInformation().chargeEftAmount().getValue().add(delta));
            }
        }
        return estimatedChargeTotal;
    }

    private static boolean isPapApplicable(PadFileModel padFileModel) {
        return padFileModel.papApplicable().getValue(Boolean.TRUE) && padFileModel.recurringEFT().getValue(true);
    }

    private static void mergeToFirstRow(BigDecimal estimatedChargeTotal, List<PadFileModel> accountPadEntities) {
        PadFileModel firstPadFileModel = null;
        int idx = 0;
        for (int i = 0; i < accountPadEntities.size(); i++) {
            PadFileModel padFileModel = accountPadEntities.get(i);
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            if (isPapApplicable(padFileModel)) {
                firstPadFileModel = padFileModel;
                firstPadFileModel._processorInformation().accountCharges().add(firstPadFileModel);
                idx = i;
                break;
            } else {
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.notUsedForACH);
            }
        }
        if (firstPadFileModel == null) {
            // Non of the records was initialized or valid
            return;
        }
        BigDecimal accountChargeTotal = firstPadFileModel._processorInformation().estimatedChargeSplit().getValue();

        BigDecimal accountEftAmountTotal = BigDecimal.ZERO;
        accountEftAmountTotal = accountEftAmountTotal.add(firstPadFileModel._processorInformation().chargeEftAmount().getValue());

        for (int i = idx + 1; i < accountPadEntities.size(); i++) {
            PadFileModel padFileModel = accountPadEntities.get(i);
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            if (isPapApplicable(padFileModel)) {
                accountChargeTotal = accountChargeTotal.add(padFileModel._processorInformation().estimatedChargeSplit().getValue());
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.mergedWithAnotherRecord);
                padFileModel._import().message().setValue(i18n.tr("Merged with row {0}", firstPadFileModel._import().row()));
                firstPadFileModel._processorInformation().accountCharges().add(padFileModel);

                accountEftAmountTotal = accountEftAmountTotal.add(padFileModel._processorInformation().chargeEftAmount().getValue());
            } else {
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.notUsedForACH);
            }
        }

        firstPadFileModel._processorInformation().estimatedChargeSplit().setValue(accountChargeTotal);
        firstPadFileModel._processorInformation().accountChargeTotal().setValue(accountChargeTotal);
        firstPadFileModel._processorInformation().accountEftAmountTotal().setValue(accountEftAmountTotal);

        BigDecimal percentNotRounded;
        if (estimatedChargeTotal.compareTo(BigDecimal.ZERO) == 0) {
            percentNotRounded = BigDecimal.ZERO;
        } else {
            percentNotRounded = accountChargeTotal.divide(estimatedChargeTotal, new MathContext(PadProcessorInformation.PERCENT_SCALE, RoundingMode.HALF_DOWN));
        }
        firstPadFileModel._processorInformation().percentNotRounded().setValue(percentNotRounded);
    }

    static String getAccount(PadFileModel padFileModel) {
        StringBuilder b = new StringBuilder();
        b.append(padFileModel.accountNumber().getValue().trim()).append(':');
        b.append(padFileModel.bankId().getValue().trim()).append(':');
        b.append(padFileModel.transitNumber().getValue().trim());
        return b.toString();
    }

    private static Map<String, List<PadFileModel>> mapByAccount(List<PadFileModel> entities) {
        Map<String, List<PadFileModel>> mappedByAccount = new LinkedHashMap<String, List<PadFileModel>>();
        for (PadFileModel padFileModel : entities) {
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            String account = getAccount(padFileModel);
            List<PadFileModel> accountEntities = mappedByAccount.get(account);
            if (accountEntities == null) {
                accountEntities = new ArrayList<PadFileModel>();
                mappedByAccount.put(account, accountEntities);
            }
            accountEntities.add(padFileModel);
        }
        return mappedByAccount;
    }

    private static void enshurePercentNotRoundedIsSet(PadFileModel padFileModel) {
        if (!padFileModel.percent().isNull() && padFileModel._processorInformation().percentNotRounded().isNull()) {
            BigDecimal percentNotRounded = new BigDecimal(padFileModel.percent().getValue()).divide(new BigDecimal(100));
            padFileModel._processorInformation().percentNotRounded().setValue(percentNotRounded);
        }
    }

    static void calulateSubsetPercentsRounding(List<PadFileModel> leasePadEntities) {
        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal percentRoundTotal = BigDecimal.ZERO;
        PadFileModel recordLargest = null;

        // All records here are for different accounts
        // Ensure there are no negative %
        PadFileModel invalid = null;
        for (PadFileModel padFileModel : leasePadEntities) {
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            enshurePercentNotRoundedIsSet(padFileModel);
            if (!padFileModel._processorInformation().percentNotRounded().isNull()) {
                if (padFileModel._processorInformation().percentNotRounded().getValue().compareTo(BigDecimal.ZERO) < 0) {
                    invalid = padFileModel;
                    padFileModel._import().invalid().setValue(Boolean.TRUE);
                    padFileModel._processorInformation().status().setValue(PadProcessingStatus.invalidResultingValues);
                    padFileModel._import().message().setValue(i18n.tr("Calculations results in negative percentage value"));
                    break;
                }
            }
        }
        if (invalid != null) {
            for (PadFileModel padFileModel : leasePadEntities) {
                if (!padFileModel._processorInformation().status().isNull()) {
                    continue;
                }
                padFileModel._import().invalid().setValue(Boolean.TRUE);
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.anotherRecordInvalid);
                padFileModel._import().message().setValue(i18n.tr("Other Pad (row {0}) on this lease is invalid", invalid._import().row()));
            }
            return;
        }

        for (PadFileModel padFileModel : leasePadEntities) {
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            if (isPapApplicable(padFileModel) && !padFileModel._processorInformation().percentNotRounded().isNull()) {
                BigDecimal percent = padFileModel._processorInformation().percentNotRounded().getValue();
                BigDecimal percentRound = percent.setScale(PadProcessorInformation.PERCENT_SCALE, BigDecimal.ROUND_HALF_UP);
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
        String zeroes = CommonsStringUtils.padding(PadProcessorInformation.PERCENT_SCALE - 1, '0');
        if ((percentTotal.compareTo(percentRoundTotal) != 0)
                && (percentTotal.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0." + zeroes + "1")) < 1)) {
            BigDecimal unapidPercentBalance = percentRoundTotal.subtract(BigDecimal.ONE);
            recordLargest._processorInformation().percent().setValue(recordLargest._processorInformation().percent().getValue().add(unapidPercentBalance));
            recordLargest._import().message().setValue("Percent value rounded remainder added to this record");
        }

        for (PadFileModel padFileModel : leasePadEntities) {
            if (!padFileModel._processorInformation().status().isNull()) {
                continue;
            }
            if (isPapApplicable(padFileModel) && !padFileModel._processorInformation().percent().isNull() && (!padFileModel.estimatedCharge().isNull())) {
                if (padFileModel._processorInformation().accountChargeTotal().isNull()) {
                    throw new Error("accountChargeTotal null for: " + padFileModel.leaseId().getValue());
                }
                BigDecimal calulatedEftAmount = padFileModel._processorInformation().accountChargeTotal().getValue();
                padFileModel._processorInformation().calulatedEftTotalAmount().setValue(DomainUtil.roundMoney(calulatedEftAmount));
            }
        }

    }

    private AutopayAgreement savePad(PadFileModel padFileModel, TenantPadCounter counters) {

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

        AutopayAgreement correspondingPap = null;

        Persistence.service().retrieve(tenant.customer());
        Persistence.service().retrieveMember(tenant.customer().paymentMethods());
        LeasePaymentMethod existingPaymentMethod = retrievePaymentMethod(tenant.customer(), details);
        if (existingPaymentMethod != null) {
            // Update PAP if one exists for existing PaymentMethod
            if (!padFileModel.charge().isNull() || !padFileModel._processorInformation().percent().isNull()) {
                List<AutopayAgreement> paps = new ArrayList<AutopayAgreement>();
                {
                    EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
                    criteria.eq(criteria.proto().paymentMethod(), existingPaymentMethod);
                    criteria.eq(criteria.proto().tenant(), tenant);
                    criteria.eq(criteria.proto().isDeleted(), false);
                    paps = Persistence.service().query(criteria);
                }
                //We do not support import of more then one PAP per PaymentMethod
                boolean sameAmountPapFound = false;
                for (AutopayAgreement pap : paps) {
                    if (!isSameAmount(pap, padFileModel)) {
                        ServerSideFactory.create(PaymentMethodFacade.class).deleteAutopayAgreement(pap);
                    } else {
                        sameAmountPapFound = true;
                        correspondingPap = pap;
                    }
                }
                if (!sameAmountPapFound) {
                    Persistence.service().persist(correspondingPap = createPAP(existingPaymentMethod, padFileModel, tenant));
                    counters.removed++;
                    counters.updated++;
                } else {
                    counters.unchanged++;
                    padFileModel._processorInformation().status().setValue(PadProcessingStatus.unchangedInDB);
                }
            } else {
                counters.unchanged++;
                padFileModel._processorInformation().status().setValue(PadProcessingStatus.unchangedInDB);
            }
        } else {
            LeasePaymentMethod method = EntityFactory.create(LeasePaymentMethod.class);
            method.isProfiledMethod().setValue(Boolean.TRUE);
            method.sameAsCurrent().setValue(Boolean.TRUE);
            method.type().setValue(PaymentType.Echeck);
            method.details().set(details);
            method.customer().set(tenant.customer());
            method.billingAddress().set(AddressRetriever.getLeaseAddressSimple(tenant.lease()));

            Persistence.service().retrieve(tenant.lease());
            Persistence.service().retrieve(tenant.lease().unit());
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(method, tenant.lease().unit().building());

            if (!padFileModel.charge().isNull() || !padFileModel._processorInformation().percent().isNull()) {
                Persistence.service().persist(correspondingPap = createPAP(method, padFileModel, tenant));
            }
            counters.imported++;
        }
        return correspondingPap;
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

    private boolean isSameAmount(AutopayAgreement pap, PadFileModel padFileModel) {
        if (pap.coveredItems().size() != padFileModel._processorInformation().accountCharges().size()) {
            return false;
        }

        for (PadFileModel charge : padFileModel._processorInformation().accountCharges()) {
            boolean found = false;
            for (AutopayAgreementCoveredItem padItem : pap.coveredItems()) {
                if (!charge.chargeCode().getValue().equals(padItem.billableItem().extraData().duplicate(YardiLeaseChargeData.class).chargeCode().getValue())) {
                    continue;
                } else if (charge._processorInformation().chargeEftAmount().getValue().compareTo(padItem.amount().getValue()) != 0) {
                    continue;
                } else {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        // all charges are found
        return true;
    }

    private AutopayAgreement createPAP(LeasePaymentMethod method, PadFileModel padFileModel, Tenant tenant) {
        AutopayAgreement pap = EntityFactory.create(AutopayAgreement.class);
        pap.paymentMethod().set(method);
        pap.tenant().set(tenant);

        Lease lease = tenant.lease();
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        if (padFileModel._processorInformation().accountCharges().size() == 0) {
            throw new Error("Charges not created for PAP");
        }

        Map<String, BillableItem> billableItems = PaymentBillableUtils.getAllBillableItems(lease.currentTerm().version());

        List<BillableItem> billableItemsProcesed = new ArrayList<BillableItem>();
        List<BillableItem> billableItemsUnprocessed = new ArrayList<BillableItem>(billableItems.values());

        for (PadFileModel charge : padFileModel._processorInformation().accountCharges()) {

            // TODO Ignore credits for now, Apply them in second pass
            if (charge._processorInformation().chargeEftAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }
            BillableItem matchingBillableItem = null;

            if (!charge._processorInformation().billableItemId().isNull()) {
                BillableItem sameIdBillableItem = billableItems.get(charge._processorInformation().billableItemId().getValue());
                if (sameIdBillableItem == null) {
                    throw new Error("BillableItem '" + charge.chargeCode().getValue() + "' " + charge._processorInformation().chargeAmount().getValue()
                            + "$ not found; already processed items " + formatBillableItems(billableItemsProcesed) + "; unprocessed "
                            + formatBillableItems(billableItemsUnprocessed));
                }
                if (charge._processorInformation().chargeAmount().getValue().compareTo(sameIdBillableItem.agreedPrice().getValue()) != 0) {
                    charge._processorInformation().actualChargeCodeAmount().setValue(sameIdBillableItem.agreedPrice().getValue());
                    throw new Error("BillableItem '" + charge.chargeCode().getValue() + "' " + charge._processorInformation().chargeAmount().getValue()
                            + "$ do not match; already processed items " + formatBillableItems(billableItemsProcesed) + "; unprocessed "
                            + formatBillableItems(billableItemsUnprocessed));
                }
                matchingBillableItem = sameIdBillableItem;
            } else {
                for (BillableItem billableItem : billableItemsUnprocessed) {
                    if (!charge.chargeCode().getValue().equals(billableItem.extraData().duplicate(YardiLeaseChargeData.class).chargeCode().getValue())) {
                        continue;
                    } else if (charge._processorInformation().chargeAmount().getValue().compareTo(billableItem.agreedPrice().getValue()) != 0) {
                        continue;
                    } else {
                        matchingBillableItem = billableItem;
                        break;
                    }
                }
            }

            if (matchingBillableItem != null) {
                billableItems.remove(matchingBillableItem);
                billableItemsProcesed.add(matchingBillableItem);

                AutopayAgreementCoveredItem padItem = EntityFactory.create(AutopayAgreementCoveredItem.class);
                padItem.billableItem().set(matchingBillableItem);
                padItem.amount().setValue(charge._processorInformation().chargeEftAmount().getValue());
                pap.coveredItems().add(padItem);
            } else {
                throw new Error("BillableItem '" + charge.chargeCode().getValue() + "' " + charge._processorInformation().chargeAmount().getValue()
                        + "$ not found; already processed items " + formatBillableItems(billableItemsProcesed) + "; unprocessed "
                        + formatBillableItems(billableItemsUnprocessed));
            }
        }

        // Process credits
        for (PadFileModel charge : padFileModel._processorInformation().accountCharges()) {
            if (charge._processorInformation().chargeEftAmount().getValue().compareTo(BigDecimal.ZERO) >= 0) {
                continue;
            }
            boolean found = false;
            for (AutopayAgreementCoveredItem padItem : pap.coveredItems()) {
                if (padItem.billableItem().equals(lease.currentTerm().version().leaseProducts().serviceItem())) {
                    found = true;
                    padItem.amount().setValue(padItem.amount().getValue().add(charge._processorInformation().chargeEftAmount().getValue()));
                    break;
                }
            }
            if (!found) {
                throw new Error("Service billableItem not found in PAP to apply credit '" + charge.chargeCode().getValue() + "' "
                        + charge._processorInformation().chargeEftAmount().getValue() + "$ not found");
            }

        }

        return pap;
    }

    private String formatBillableItems(Collection<BillableItem> billableItemsProcesed) {
        StringBuilder b = new StringBuilder();
        for (BillableItem billableItem : billableItemsProcesed) {
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append("'").append(billableItem.extraData().duplicate(YardiLeaseChargeData.class).chargeCode().getValue()).append("' ");
            b.append(billableItem.agreedPrice().getValue()).append("$ ");
        }
        return b.toString();
    }
}
