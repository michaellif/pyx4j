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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.MerchantAccountFileModel;
import com.propertyvista.interfaces.importer.parser.MerchantAccountParser;
import com.propertyvista.server.jobs.TaskRunner;

public class MerchantAccountProcessor {

    private MerchantAccount retrievedAccount = null;

    private final static Logger log = LoggerFactory.getLogger(MerchantAccountProcessor.class);

    private List<MerchantAccountFileModel> accounts = new ArrayList<MerchantAccountFileModel>();

    MerchantAccountCounter counters = new MerchantAccountCounter();

    public String persistMerchantAccounts(byte[] data, DownloadFormat format) {
        MerchantAccountCounter counters = new MerchantAccountCounter();
        accounts = new MerchantAccountParser().parseFile(data, format);
        counters.add(saveMerchantAccounts(accounts));

        String message = SimpleMessageFormat.format("{0} merchant accounts created, {1} skipped, {2} updated. {3} buildings affected.", counters.imported,
                counters.skipped, counters.updated, counters.buildingsAffected);
        log.info(message);
        return message;
    }

    private MerchantAccountCounter saveMerchantAccounts(List<MerchantAccountFileModel> entities) {

        for (final MerchantAccountFileModel model : entities) {

            retrievedAccount = null;

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Throwable>() {

                    @Override
                    public Void execute() {

                        Pmc pmc = EntityFactory.create(Pmc.class);
                        {
                            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                            criteria.eq(criteria.proto().namespace(), model.pmc().getValue());
                            pmc = Persistence.service().retrieve(criteria);
                        }

                        if (model.pmc() != null) {

                            List<PmcMerchantAccountIndex> indexes = new ArrayList<PmcMerchantAccountIndex>();
                            {
                                EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                                criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), pmc));
                                indexes = Persistence.service().query(criteria);
                            }

                            for (final PmcMerchantAccountIndex index : indexes) {
                                TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
                                    @Override
                                    public Void call() {
                                        MerchantAccount rAccount = EntityFactory.create(MerchantAccount.class);
                                        {
                                            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
                                            criteria.eq(criteria.proto().id(), index.merchantAccountKey());
                                            rAccount = Persistence.service().retrieve(criteria);
                                        }

                                        if (rAccount.bankId().getValue().equals(model.bankId().getValue().replaceAll("\\D", ""))
                                                && rAccount.accountNumber().getValue().equals(model.accountNumber().getValue().replaceAll("\\D", ""))
                                                && rAccount.branchTransitNumber().getValue().equals(model.transitNumber().getValue().replaceAll("\\D", ""))) {
                                            retrievedAccount = rAccount;
                                        }
                                        return null;
                                    }
                                });
                            }

                            if (retrievedAccount != null) {
                                if (retrievedAccount.merchantTerminalId() != null) {
                                    if (retrievedAccount.merchantTerminalId().getValue().equals(model.terminalId().getValue())) {
                                        if (model.propertyCode() != null && !model.propertyCode().isNull()) {
                                            setAccountInBuilding(model.propertyCode().getValue(), retrievedAccount, pmc);
                                        }
                                        log.info(SimpleMessageFormat.format("Record skipped at sheet {0}, row {1}", model._import().sheet().getValue(), model
                                                ._import().row().getValue()));
                                        counters.skipped++;
                                    } else {
                                        throw new Error(
                                                SimpleMessageFormat
                                                        .format("The account from sheet {0}, row {1} already exists in the database with a terminal ID {2}. Please make sure your information is correct.",
                                                                model._import().sheet().getValue(), model._import().row().getValue(), retrievedAccount
                                                                        .merchantTerminalId().getValue()));
                                    }
                                } else {
                                    retrievedAccount.merchantTerminalId().setValue(model.terminalId().getValue());
                                    if (model.propertyCode() != null && !model.propertyCode().isNull()) {
                                        setAccountInBuilding(model.propertyCode().getValue(), retrievedAccount, pmc);
                                    }
                                    log.info(SimpleMessageFormat.format("Terminal ID value updated from sheet {0}, row {1}",
                                            model._import().sheet().getValue(), model._import().row().getValue()));
                                    counters.updated++;
                                }
                            } else {

                                final MerchantAccount account = EntityFactory.create(MerchantAccount.class);
                                account.accountNumber().setValue(model.accountNumber().getValue().replaceAll("\\D", ""));
                                account.bankId().setValue(model.bankId().getValue().replaceAll("\\D", ""));
                                account.branchTransitNumber().setValue(model.transitNumber().getValue().replaceAll("\\D", ""));
                                account.merchantTerminalId().setValue(model.terminalId().getValue());
                                account.status().setValue(MerchantAccountActivationStatus.Active);
                                ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, account);
                                if (model.propertyCode() != null && !model.propertyCode().isNull()) {
                                    setAccountInBuilding(model.propertyCode().getValue(), account, pmc);
                                }
                                counters.imported++;
                            }

                        } else {
                            // TODO nothing so far, assume all accounts come with property code and pmc info.
                        }
                        return null;
                    }
                });
            } catch (Throwable t) {
                log.error("Error", t);
                throw new UserRuntimeException(SimpleMessageFormat.format("Error during execution at sheet {0}, row {1}: {2}", model._import().sheet()
                        .getValue(), model._import().row().getValue(), t));
            }
        }

        return counters;
    }

    protected void setAccountInBuilding(final String propertyCode, final MerchantAccount account, Pmc pmc) {
        TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
            @Override
            public Void call() {
                Building building = EntityFactory.create(Building.class);
                {
                    EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                    criteria.eq(criteria.proto().propertyCode(), propertyCode);
                    building = Persistence.service().retrieve(criteria);
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
                        && retrievedAccount.branchTransitNumber().getValue().equals(account.branchTransitNumber().getValue())
                        && retrievedAccount.accountNumber().getValue().equals(account.accountNumber().getValue())
                        && retrievedAccount.merchantTerminalId().getValue().equals(account.merchantTerminalId().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public class MerchantAccountCounter {

        public int imported;

        public int skipped;

        public int updated;

        public int buildingsAffected;

        public MerchantAccountCounter() {
            this.imported = 0;
            this.skipped = 0;
            this.updated = 0;
            this.buildingsAffected = 0;
        }

        public void add(MerchantAccountCounter counters) {
            this.imported += counters.imported;
            this.skipped += counters.skipped;
            this.updated += counters.updated;
            this.buildingsAffected += counters.buildingsAffected;
        }
    }
}
