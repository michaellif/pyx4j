/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.operations.client.ui;

import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.sidemenu.SideMenu;
import com.pyx4j.site.client.ui.sidemenu.SideMenuAppPlaceItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class NavigViewImpl extends Composite implements NavigView {

    private static final I18n i18n = I18n.get(NavigViewImpl.class);

    private final SideMenu menu;

    public NavigViewImpl() {

        SideMenuList root = new SideMenuList();
        menu = new SideMenu(root);
        initWidget(menu);

        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        {//PMC Management
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("PMC Management"), null, null));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.PMC(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.PmcMerchantAccount(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.Trigger(), VistaOperationsBehavior.ProcessAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.TriggerRun(), VistaOperationsBehavior.ProcessAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.TriggerRunData(), VistaOperationsBehavior.ProcessAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.BillingSetup(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Management.CreditCheckTransaction(), VistaOperationsBehavior.SystemAdmin));
        }

        {//Funds Transfer
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Funds Transfer"), null, null));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.CardTransactionRecord(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.DirectDebitRecord(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.FundsTransferFile(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.FundsTransferBatch(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.FundsTransferRecord(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationFile(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationSummary(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationRecord(), VistaOperationsBehavior.Caledon));

        }

        {//Security
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Security"), null, null));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Security.AuditRecord(), VistaOperationsBehavior.SystemAdmin));
        }

        {//Legal
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Legal"), null, null));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.PmcTerms(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.PmcPaymentPadTerms(), VistaOperationsBehavior.SystemAdmin));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.PmcCaledonTermsTemplate(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.PmcCaledonSoleProprietorshipSection(), VistaOperationsBehavior.SystemAdmin));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.ProspectPortalTermsAndConditions(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.ProspectPortalPrivacyPolicy(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.ResidentPortalTermsAndConditions(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.ResidentPortalPrivacyPolicy(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.TenantBillingTerms(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.TenantPreAuthorizedPaymentECheck(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.TenantPreAuthorizedPaymentCreditCard(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.TenantCaledonConvenienceFee(), VistaOperationsBehavior.SystemAdmin));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Legal.TenantSurePapAgreement(), VistaOperationsBehavior.SystemAdmin));

        }

        {//Administration
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Administration"), null, null));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.Maintenance(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.Simulation(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.AdminUsers(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.OperationsAlert(), VistaOperationsBehavior.SystemAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.EncryptedStorage(), VistaOperationsBehavior.SecurityAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.TenantSure(), VistaOperationsBehavior.SecurityAdmin));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Administration.OutgoingMail(), VistaOperationsBehavior.SecurityAdmin));
        }

        if (ApplicationMode.isDevelopment()) {//Dev Simulation
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Dev Simulation"), null, null));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.SimulatedDataPreload(), VistaOperationsBehavior.SecurityAdmin));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.PadSimulation.PadSimFile(), VistaOperationsBehavior.Caledon));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulatorConfiguration()
                    .formViewerPlace(new Key(1)), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationMerchantAccount(),
                    VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationCard(),
                    VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationTransaction(),
                    VistaOperationsBehavior.Caledon));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationReconciliation(),
                    VistaOperationsBehavior.Caledon));

            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.DirectBankingSimRecord(), VistaOperationsBehavior.Caledon));
            list.addMenuItem(new SideMenuAppPlaceItem(new OperationsSiteMap.Simulator.DirectBankingSimFile(), VistaOperationsBehavior.Caledon));
        }

    }

    @Override
    public void select(AppPlace appPlace) {
        menu.select(appPlace);
    }
}
