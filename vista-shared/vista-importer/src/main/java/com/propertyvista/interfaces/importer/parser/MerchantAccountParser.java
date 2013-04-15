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
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.MerchantAccountFileModel;
import com.propertyvista.server.jobs.TaskRunner;

public class MerchantAccountParser {

    private MerchantAccount retrievedAccount = null;

    private final static Logger log = LoggerFactory.getLogger(MerchantAccountParser.class);

    private static final I18n i18n = I18n.get(MerchantAccountParser.class);

    private List<MerchantAccountFileModel> pads = new ArrayList<MerchantAccountFileModel>();

    public String persistMerchantAccounts(byte[] data, DownloadFormat format) {
        MerchantAccountCounter counters = new MerchantAccountCounter();
        pads = parseFile(data, format);
        counters.add(saveMerchantAccounts(pads));

        String message = SimpleMessageFormat.format("{0} merchant accounts created, {1} skipped, {2} updated", counters.imported, counters.skipped,
                counters.updated);
        log.info(message);
        return message;
    }

    private List<MerchantAccountFileModel> parseFile(byte[] data, DownloadFormat format) {
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
            EntityCSVReciver<MerchantAccountFileModel> receiver = new MerchantAccountFileCSVReciver(loader.getSheetName(sheetNumber));
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

    private MerchantAccountCounter saveMerchantAccounts(List<MerchantAccountFileModel> entities) {
        final MerchantAccountCounter counters = new MerchantAccountCounter();

        for (final MerchantAccountFileModel model : entities) {

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

                        if (model.pmc() != null && model.propertyCode() != null) {

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

                                        if (rAccount.bankId().getValue().equals(model.bankId().getValue())
                                                && rAccount.accountNumber().getValue().equals(model.accountNumber().getValue())
                                                && rAccount.branchTransitNumber().getValue().equals(model.transitNumber().getValue())) {
                                            retrievedAccount = rAccount;
                                        }
                                        return null;
                                    }
                                });
                            }

                            if (retrievedAccount != null) {
                                if (retrievedAccount.merchantTerminalId() != null) {
                                    if (retrievedAccount.merchantTerminalId().getValue().equals(model.terminalId().getValue())) {
                                        log.info(SimpleMessageFormat.format("Record skipped at sheet {0}, row {1}", model._import().sheet().getValue(), model
                                                ._import().row().getValue()));
                                        counters.skipped++;
                                    } else {
                                        retrievedAccount.merchantTerminalId().setValue(model.terminalId().getValue());
                                        log.info(SimpleMessageFormat.format("Terminal ID value updated from sheet {0}, row {1}", model._import().sheet()
                                                .getValue(), model._import().row().getValue()));
                                        counters.updated++;
                                    }
                                } else {
                                    throw new Error(
                                            SimpleMessageFormat
                                                    .format("The account from sheet {0}, row {1} already exists in the database with a terminal ID {2}. Please make sure your information is correct.",
                                                            model._import().sheet().getValue(), model._import().row().getValue(), retrievedAccount
                                                                    .merchantTerminalId().getValue()));
                                }
                            } else {

                                final MerchantAccount account = EntityFactory.create(MerchantAccount.class);
                                account.accountNumber().setValue(model.accountNumber().getValue());
                                account.bankId().setValue(model.bankId().getValue());
                                account.branchTransitNumber().setValue(model.transitNumber().getValue());
                                account.merchantTerminalId().setValue(model.terminalId().getValue());
                                account.status().setValue(MerchantAccountActivationStatus.Active);
                                ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, account);
                                TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
                                    @Override
                                    public Void call() {
                                        Building building = EntityFactory.create(Building.class);
                                        {
                                            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                                            criteria.eq(criteria.proto().propertyCode(), model.propertyCode().getValue());
                                            building = Persistence.service().retrieve(criteria);
                                        }

                                        Persistence.service().retrieveMember(building.merchantAccounts());
                                        building.merchantAccounts().clear();
                                        BuildingMerchantAccount bma = building.merchantAccounts().$();
                                        bma.merchantAccount().set(account);
                                        building.merchantAccounts().add(bma);
                                        Persistence.service().persist(building);
                                        counters.imported++;
                                        return null;
                                    }
                                });
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

    private static class MerchantAccountFileCSVReciver extends EntityCSVReciver<MerchantAccountFileModel> {
        String sheetNumber;

        public MerchantAccountFileCSVReciver(String sheetName) {
            super(MerchantAccountFileModel.class);
            this.sheetNumber = sheetName;
            this.setMemberNamesAsHeaders(false);
            this.setHeaderLinesCount(1, 2);
            this.setHeadersMatchMinimum(3);
            this.setVerifyRequiredHeaders(true);
            this.setVerifyRequiredValues(true);
        }

        @Override
        public void onRow(MerchantAccountFileModel entity) {
            if (!entity.isNull()) {
                entity._import().row().setValue(getCurrentRow());
                entity._import().sheet().setValue(sheetNumber);
                super.onRow(entity);
            }
        }

    }

    public class MerchantAccountCounter {

        public int imported;

        public int skipped;

        public int updated;

        public MerchantAccountCounter() {
            this.imported = 0;
            this.skipped = 0;
            this.updated = 0;
        }

        public void add(MerchantAccountCounter counters) {
            this.imported += counters.imported;
            this.skipped += counters.skipped;
            this.updated += counters.updated;
        }
    }
}
