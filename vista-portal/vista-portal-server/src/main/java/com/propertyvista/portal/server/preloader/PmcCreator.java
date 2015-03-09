/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 23, 2012
 * @author vlads
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.biz.preloader.PmcPreloaderFacade;
import com.propertyvista.biz.preloader.UserPreloaderFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.server.TaskRunner;

public class PmcCreator {

    private final static Logger log = LoggerFactory.getLogger(PmcCreator.class);

    public static void preloadPmc(final Pmc pmc) {
        TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
            @Override
            public Void call() {

                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        RDBUtils.ensureNamespace();
                        if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                            RDBUtils.initAllEntityTables();
                        }
                        return null;
                    }

                });

                if (ApplicationMode.isDemo()) {
                    ServerSideFactory.create(PmcPreloaderFacade.class).preloadExistingPmc(pmc);
                } else {
                    AbstractDataPreloader preloader = VistaDataPreloaders.productionPmcPreloaders();
                    preloader.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());
                    log.info("Preload {}", preloader.create());
                }

                CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();

                for (OnboardingUser onbUser : getAllOnboardingUsers(pmc)) {
                    ServerSideFactory.create(UserPreloaderFacade.class).createCrmEmployee(onbUser.firstName().getValue(), onbUser.lastName().getValue(),
                            onbUser.email().getValue(), onbUser.password().getValue(), onbUser.getPrimaryKey(), defaultRole);
                }

                // Create support account by default
                ServerSideFactory.create(UserPreloaderFacade.class).createVistaSupportUsers();

                if (ApplicationMode.isDevelopment() && !ApplicationMode.isDemo()) {
                    for (int i = 1; i <= DemoData.UserType.PM.getDefaultMax(); i++) {
                        String email = DemoData.UserType.PM.getEmail(i);
                        ServerSideFactory.create(UserPreloaderFacade.class).createCrmEmployee(email, email, email, email, null, defaultRole);
                    }
                }

                return null;
            }
        });

    }

    private static List<OnboardingUser> getAllOnboardingUsers(final Pmc pmc) {
        return TaskRunner.runInOperationsNamespace(new Callable<List<OnboardingUser>>() {
            @Override
            public List<OnboardingUser> call() {
                EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
                criteria.eq(criteria.proto().pmc(), pmc);
                return Persistence.service().query(criteria);
            }
        });
    }

}
