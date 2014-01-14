/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.generator.PreloadData;
import com.propertyvista.preloader.ido.MerchantAccountImport;
import com.propertyvista.server.TaskRunner;

public class MerchantAccountPreloader extends BaseVistaDevDataPreloader {

    private static List<MerchantAccountImport> accountImport;

    @Override
    public String create() {
        if (accountImport == null) {
            EntityCSVReciver<MerchantAccountImport> rcv = EntityCSVReciver.create(MerchantAccountImport.class);
            rcv.setHeaderIgnoreCase(true);
            accountImport = rcv.loadResourceFile("MerchantAccountsImport.csv");
        }

        final Pmc pmc = VistaDeployment.getCurrentPmc().duplicate();

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {

                Persistence.service().retrieveMember(pmc.paymentTypeInfo());
                int ordinal = -1;
                try {
                    DemoPmc pmcId = DemoData.DemoPmc.valueOf(pmc.namespace().getValue());
                    ordinal = pmcId.ordinal();
                } catch (IllegalArgumentException ignore) {
                }
                if (ordinal >= 0) {
                    String caledonCompanyId = "BIRCHWOODTEST";
                    List<MerchantAccountImport> companyImport = new ArrayList<MerchantAccountImport>();
                    for (MerchantAccountImport imp : accountImport) {
                        if (caledonCompanyId.equals(imp.companyId().getValue())) {
                            companyImport.add(imp);
                            pmc.paymentTypeInfo().eChequeFee().setValue(new BigDecimal(stripDollars(imp.transactionFee().getValue())));
                        }
                    }
                    Persistence.service().persist(pmc.paymentTypeInfo());

                    // Add one or two (for vista) working accounts.
                    int nWorkingAccounts = 1;
                    if (ordinal == 0) {
                        nWorkingAccounts = 2;
                    }
                    for (int n = 0; n < nWorkingAccounts; n++) {
                        int lineN = ordinal + n;
                        if (ordinal != 0) {
                            lineN += 1;
                        }
                        if (companyImport.size() > lineN) {
                            MerchantAccountImport imp = companyImport.get(lineN);
                            MerchantAccount merchantAccount = EntityFactory.create(MerchantAccount.class);
                            merchantAccount.merchantTerminalId().setValue(imp.merchantTerminalId().getValue());
                            merchantAccount.merchantTerminalIdConvenienceFee().setValue(imp.merchantTerminalIdConvenienceFee().getValue());
                            merchantAccount.bankId().setValue(imp.bankId().getValue());
                            merchantAccount.branchTransitNumber().setValue(imp.branchTransitNumber().getValue());
                            merchantAccount.accountNumber().setValue(imp.accountNumber().getValue());
                            merchantAccount.chargeDescription().setValue("Pay for " + pmc.name().getValue() + " " + n);
                            merchantAccount.invalid().setValue(Boolean.FALSE);
                            merchantAccount.status().setValue(MerchantAccount.MerchantAccountActivationStatus.Active);

                            merchantAccount.setup().acceptedEcheck().setValue(true);
                            merchantAccount.setup().acceptedDirectBanking().setValue(true);
                            merchantAccount.setup().acceptedCreditCard().setValue(true);
                            merchantAccount.setup().acceptedCreditCardConvenienceFee().setValue(true);
                            merchantAccount.setup().acceptedInterac().setValue(true);

                            ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, merchantAccount);
                        }
                    }

                    int internalAccounts = 3;

                    for (int n = 0; n <= internalAccounts; n++) {
                        MerchantAccount merchantAccount = EntityFactory.create(MerchantAccount.class);
                        merchantAccount.merchantTerminalId().setValue("EBIRCH" + ordinal + n);
                        merchantAccount.bankId().setValue(ordinal + "00");
                        merchantAccount.branchTransitNumber().setValue("0100" + n);
                        merchantAccount.status().setValue(MerchantAccount.MerchantAccountActivationStatus.Active);

                        merchantAccount.setup().acceptedEcheck().setValue(true);
                        merchantAccount.setup().acceptedDirectBanking().setValue(true);
                        merchantAccount.setup().acceptedCreditCard().setValue(true);
                        merchantAccount.setup().acceptedCreditCardConvenienceFee().setValue(true);
                        merchantAccount.setup().acceptedInterac().setValue(true);

                        // Make one ElectronicPaymentsAllowed FALSE
                        if (n == internalAccounts) {
                            merchantAccount.accountNumber().setValue(PreloadData.ElectronicPaymentsNotAllowedAccountPrefix + "876543");
                            merchantAccount.invalid().setValue(Boolean.TRUE);
                        } else {
                            merchantAccount.accountNumber().setValue("01234567");
                            merchantAccount.invalid().setValue(Boolean.FALSE);
                        }

                        merchantAccount.chargeDescription().setValue("Pay for " + pmc.name().getValue() + " er " + n);
                        ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, merchantAccount);
                    }

                } else {
                    int internalAccounts = 2;
                    int offsetNumber = Persistence.service().count(EntityQueryCriteria.create(PmcMerchantAccountIndex.class));
                    for (int n = 0; n <= internalAccounts; n++) {
                        MerchantAccount merchantAccount = EntityFactory.create(MerchantAccount.class);
                        merchantAccount.merchantTerminalId().setValue("tD" + String.valueOf(offsetNumber) + String.valueOf(n));
                        merchantAccount.merchantTerminalIdConvenienceFee().setValue("tC" + String.valueOf(offsetNumber) + String.valueOf(n));
                        merchantAccount.bankId().setValue("001");
                        merchantAccount.branchTransitNumber().setValue("3" + String.valueOf(offsetNumber));
                        merchantAccount.status().setValue(MerchantAccount.MerchantAccountActivationStatus.Active);

                        merchantAccount.setup().acceptedEcheck().setValue(true);
                        merchantAccount.setup().acceptedDirectBanking().setValue(true);
                        merchantAccount.setup().acceptedCreditCard().setValue(true);
                        merchantAccount.setup().acceptedCreditCardConvenienceFee().setValue(true);
                        merchantAccount.setup().acceptedInterac().setValue(true);

                        // Make one ElectronicPaymentsAllowed FALSE
                        if (n == internalAccounts) {
                            merchantAccount.accountNumber().setValue(
                                    PreloadData.ElectronicPaymentsNotAllowedAccountPrefix + String.valueOf(offsetNumber) + "789");
                            merchantAccount.invalid().setValue(Boolean.TRUE);
                        } else {
                            merchantAccount.accountNumber().setValue(String.valueOf(n) + "998");
                            merchantAccount.invalid().setValue(Boolean.FALSE);
                        }
                        merchantAccount.chargeDescription().setValue("Pay for " + pmc.name().getValue() + " er " + n);
                        ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, merchantAccount);
                    }
                }

                return null;
            }
        });

        return null;
    }

    private static String stripDollars(String value) {
        if (value.startsWith("$")) {
            return value.substring(1);
        } else {
            return value;
        }
    }

    @Override
    public String delete() {
        return null;
    }

}
