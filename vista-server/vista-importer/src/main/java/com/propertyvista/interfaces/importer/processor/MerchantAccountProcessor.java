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
package com.propertyvista.interfaces.importer.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.interfaces.importer.model.MerchantAccountFileModel;
import com.propertyvista.server.TaskRunner;

public class MerchantAccountProcessor {

    private final static Logger log = LoggerFactory.getLogger(MerchantAccountProcessor.class);

    private final MerchantAccountCounter counters = new MerchantAccountCounter();

    public String persistMerchantAccounts(List<MerchantAccountFileModel> model) {
        saveMerchantAccounts(model);

        String message = SimpleMessageFormat.format("{0} merchant accounts created, {1} MID Not Changed, {2} MID updated. {3} buildings updated",
                counters.imported, counters.notChanged, counters.updated, counters.buildingsAffected);

        if (counters.invalid != 0 || counters.notFound != 0 || counters.buildingNotFound != 0) {
            message += SimpleMessageFormat.format(", {0} invalid records, {1} pmc not found, {2} building not found", counters.invalid, counters.notFound,
                    counters.buildingNotFound);
        }
        message += ".";
        log.info(message);

        return message;
    }

    private MerchantAccountCounter saveMerchantAccounts(List<MerchantAccountFileModel> entities) {
        for (final MerchantAccountFileModel model : entities) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Throwable>() {

                    @Override
                    public Void execute() {
                        processOneRow(model);
                        return null;
                    }
                });
            } catch (Throwable t) {
                log.error("Error", t);
                throw new UserRuntimeException(SimpleMessageFormat.format("Error during execution at sheet {0}, row {1}: {2}", model._import().sheet(), model
                        ._import().row(), t));
            }
        }
        return counters;
    }

    private void trimValue(IPrimitive<String> value) {
        if (!value.isNull()) {
            value.setValue(value.getValue().replace("\\D", "").trim());
        }
    }

    private void processOneRow(final MerchantAccountFileModel model) {
        trimValue(model.accountNumber());
        trimValue(model.bankId());
        trimValue(model.transitNumber());
        trimValue(model.terminalId());
        trimValue(model.propertyCode());

        // TODO validate the records
        String validationMessage = getValidationMessage(model);
        if (validationMessage != null) {
            addStatus(model, validationMessage);
            counters.invalid++;
            return;
        }

        Pmc pmc;
        {
            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.eq(criteria.proto().namespace(), model.pmc().getValue());
            pmc = Persistence.service().retrieve(criteria);
            if (pmc == null) {
                counters.notFound++;
                return;
            }
        }
        model.processingStatus().setValue("");

        {
            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.eq(criteria.proto().merchantTerminalId(), model.terminalId());
            criteria.ne(criteria.proto().pmc(), pmc);
            PmcMerchantAccountIndex otherPmcAccount = Persistence.service().retrieve(criteria);

            if (otherPmcAccount != null) {
                addStatus(model, "Terminal ID already belong to another PMC {0}", otherPmcAccount.pmc().name());
                counters.invalid++;
                return;
            }
        }

        if (!model.accountNumber().isNull()) {
            validationMessage = getAccountNumberValidationMessage(model);
            if (validationMessage != null) {
                addStatus(model, validationMessage);
                counters.invalid++;
                return;
            }
            processUpdateByAccountNumber(pmc, model);
        } else {
            processUpdateByTerminalId(pmc, model);
        }
    }

    private void processUpdateByTerminalId(Pmc pmc, final MerchantAccountFileModel model) {
        final MerchantAccount retrievedAccount = TaskRunner.runInTargetNamespace(pmc, new Callable<MerchantAccount>() {
            @Override
            public MerchantAccount call() {
                EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
                criteria.eq(criteria.proto().merchantTerminalId(), model.terminalId());
                return Persistence.service().retrieve(criteria);
            }
        });

        if (!retrievedAccount.merchantTerminalIdConvenienceFee().equals(model.merchantTerminalIdConvenienceFee())) {
            retrievedAccount.merchantTerminalIdConvenienceFee().setValue(model.merchantTerminalIdConvenienceFee().getValue());
            retrievedAccount.setup().acceptedCreditCardConvenienceFee().setValue(!retrievedAccount.merchantTerminalIdConvenienceFee().isNull());
            ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, retrievedAccount);
            addStatus(model, "Terminal ID Convenience Fee value updated.");
            counters.updated++;
        } else {
            addStatus(model, "Terminal ID Record is not updated.");
            counters.notChanged++;
        }
    }

    private void processUpdateByAccountNumber(Pmc pmc, final MerchantAccountFileModel model) {
        List<PmcMerchantAccountIndex> indexes = new ArrayList<PmcMerchantAccountIndex>();
        {
            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.eq(criteria.proto().pmc(), pmc);
            indexes = Persistence.service().query(criteria);
        }

        final MerchantAccount retrievedAccount = EntityFactory.create(MerchantAccount.class);

        // Same account
        for (final PmcMerchantAccountIndex index : indexes) {
            TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
                @Override
                public Void call() {
                    MerchantAccount rAccount;
                    {
                        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
                        criteria.eq(criteria.proto().id(), index.merchantAccountKey());
                        rAccount = Persistence.service().retrieve(criteria);
                    }

                    if (rAccount.bankId().equals(model.bankId()) && rAccount.accountNumber().equals(model.accountNumber())
                            && rAccount.branchTransitNumber().equals(model.transitNumber())) {
                        retrievedAccount.set(rAccount);
                    }
                    return null;
                }
            });
            if (!retrievedAccount.isNull()) {
                break;
            }
        }

        // Moving merchantTerminalId to different account ?
        PmcMerchantAccountIndex otherAccountWithSameMID = null;
        {
            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.eq(criteria.proto().merchantTerminalId(), model.terminalId());
            criteria.eq(criteria.proto().pmc(), pmc);
            otherAccountWithSameMID = Persistence.service().retrieve(criteria);
        }

        if ((otherAccountWithSameMID != null) && (!otherAccountWithSameMID.merchantAccountKey().equals(retrievedAccount.id()))) {
            addStatus(model, "Terminal ID already assigned to different account {0} in this PMC", retrievedAccount);
            counters.invalid++;
            return;
        }

        if (!retrievedAccount.isNull()) {
            if (!retrievedAccount.merchantTerminalId().isNull()) {
                if (retrievedAccount.merchantTerminalId().getValue().equals(model.terminalId().getValue())) {
                    addStatus(model, "Terminal ID Record is not updated.");

                    if (!retrievedAccount.merchantTerminalIdConvenienceFee().equals(model.merchantTerminalIdConvenienceFee())) {
                        retrievedAccount.merchantTerminalIdConvenienceFee().setValue(model.merchantTerminalIdConvenienceFee().getValue());
                        addStatus(model, "Terminal ID Convenience Fee value updated.");
                        ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, retrievedAccount);
                    } else {
                        counters.notChanged++;
                    }

                    setAccountInBuilding(model, retrievedAccount, pmc);
                } else {
                    addStatus(model, "The account already exists in the database with a terminal ID {0}. Please make sure your information is correct.",
                            retrievedAccount.merchantTerminalId());
                }
            } else {
                retrievedAccount.merchantTerminalId().setValue(model.terminalId().getValue());
                retrievedAccount.merchantTerminalIdConvenienceFee().setValue(model.merchantTerminalIdConvenienceFee().getValue());
                addStatus(model, "Terminal ID value updated.");
                ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, retrievedAccount);

                setAccountInBuilding(model, retrievedAccount, pmc);

                counters.updated++;
            }
        } else {

            final MerchantAccount account = EntityFactory.create(MerchantAccount.class);
            account.accountNumber().setValue(model.accountNumber().getValue());
            account.bankId().setValue(model.bankId().getValue());
            account.branchTransitNumber().setValue(model.transitNumber().getValue());
            account.merchantTerminalId().setValue(model.terminalId().getValue());
            account.merchantTerminalIdConvenienceFee().setValue(model.merchantTerminalIdConvenienceFee().getValue());
            account.status().setValue(MerchantAccountActivationStatus.Active);

            account.setup().acceptedEcheck().setValue(true);
            account.setup().acceptedDirectBanking().setValue(true);
            account.setup().acceptedCreditCard().setValue(true);
            account.setup().acceptedCreditCardConvenienceFee().setValue(!account.merchantTerminalIdConvenienceFee().isNull());
            account.setup().acceptedInterac().setValue(true);

            ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, account);

            setAccountInBuilding(model, account, pmc);

            counters.imported++;
        }
    }

    private String getValidationMessage(MerchantAccountFileModel model) {
        if (model.pmc().isNull()) {
            return "PMC is required";
        }
        if (model.terminalId().isNull()) {
            return "Terminal ID is required";
        }
        return null;
    }

    private String getAccountNumberValidationMessage(MerchantAccountFileModel model) {
        if (model.accountNumber().isNull()) {
            return "Account Number is required";
        }
        if (!ValidationUtils.isAccountNumberValid(model.accountNumber().getValue())) {
            return "Account Number should consist of up to 12 digits";
        }
        if (model.bankId().isNull()) {
            return "Bank Id/Institution is required";
        }
        if (!ValidationUtils.isBankIdNumberValid(model.bankId().getValue())) {
            return "Bank Id/Institution should consist of 3 digits";
        }
        if (model.transitNumber().isNull()) {
            return "Transit Number is required";
        }
        if (!ValidationUtils.isBranchTransitNumberValid(model.transitNumber().getValue())) {
            return "Transit Number should consist of 5 digits";
        }
        return null;
    }

    private void addStatus(final MerchantAccountFileModel model, final String fmt, Object... arguments) {
        StringBuilder b = new StringBuilder();
        if (!model.processingStatus().isNull()) {
            b.append(model.processingStatus().getValue());
        }
        if (b.length() > 0) {
            b.append(" ");
        }
        b.append(SimpleMessageFormat.format(fmt, arguments));
        model.processingStatus().setValue(b.toString());
    }

    private void setAccountInBuilding(final MerchantAccountFileModel model, final MerchantAccount account, Pmc pmc) {
        if (model.propertyCode().isNull()) {
            return;
        }
        TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
            @Override
            public Void call() {
                Building building;
                {
                    EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                    criteria.eq(criteria.proto().propertyCode(), model.propertyCode().getValue().trim());
                    building = Persistence.service().retrieve(criteria);
                }

                if (building == null) {
                    counters.buildingNotFound++;
                    addStatus(model, "Building with property code {0} not found in the database.", model.propertyCode().getValue().trim());
                    return null;
                }
                Persistence.service().retrieveMember(building.merchantAccounts());
                if (!building.merchantAccounts().isNull() && !building.merchantAccounts().isEmpty()) {
                    MerchantAccount retrievedAccount = building.merchantAccounts().iterator().next().merchantAccount();
                    if (accountAlreadySet(retrievedAccount, account)) {
                        return null;
                    }
                }
                building.merchantAccounts().clear();
                BuildingMerchantAccount bma = building.merchantAccounts().$();
                bma.merchantAccount().set(account);
                building.merchantAccounts().add(bma);
                Persistence.service().persist(building);
                counters.buildingsAffected++;
                return null;
            }
        });
    }

    private boolean accountAlreadySet(MerchantAccount retrievedAccount, MerchantAccount account) {
        if (retrievedAccount != null && !retrievedAccount.isNull()) {
            if (retrievedAccount.bankId() != null && !retrievedAccount.bankId().isNull() && retrievedAccount.branchTransitNumber() != null
                    && !retrievedAccount.branchTransitNumber().isNull() && retrievedAccount.accountNumber() != null
                    && !retrievedAccount.accountNumber().isNull() && retrievedAccount.merchantTerminalId() != null
                    && !retrievedAccount.merchantTerminalId().isNull()) {
                if (retrievedAccount.bankId().getValue().equals(account.bankId().getValue())
                        && retrievedAccount.branchTransitNumber().equals(account.branchTransitNumber())
                        && retrievedAccount.accountNumber().equals(account.accountNumber())
                        && retrievedAccount.merchantTerminalId().equals(account.merchantTerminalId())) {
                    return true;
                }
            }
        }
        return false;
    }

    static class MerchantAccountCounter {

        int invalid;

        int notFound;

        int imported;

        int notChanged;

        int updated;

        int buildingsAffected;

        int buildingNotFound;
    }
}
