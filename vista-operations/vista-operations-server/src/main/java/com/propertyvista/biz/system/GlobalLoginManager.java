/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 15, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.concurrent.Callable;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.account.GlobalLoginResponseDTO;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.domain.security.GlobalCrmUserIndex;
import com.propertyvista.server.jobs.TaskRunner;

class GlobalLoginManager {

    private static final I18n i18n = I18n.get(GlobalLoginManager.class);

    GlobalLoginResponseDTO findAndVerifyCrmUser(final String email, final String password) {
        return TaskRunner.runInOperationsNamespace(new Callable<GlobalLoginResponseDTO>() {
            @Override
            public GlobalLoginResponseDTO call() {
                EntityQueryCriteria<GlobalCrmUserIndex> criteria = EntityQueryCriteria.create(GlobalCrmUserIndex.class);
                criteria.eq(criteria.proto().email(), EmailValidator.normalizeEmailAddress(email));
                for (GlobalCrmUserIndex idx : Persistence.service().query(criteria)) {

                    CrmUser user = verifyCrmUser(idx, password);
                    if (user != null) {
                        GlobalLoginResponseDTO responce = EntityFactory.create(GlobalLoginResponseDTO.class);
                        responce.user().set(user);
                        responce.pmc().set(idx.pmc());
                        return responce;
                    }

                }
                return null;
            }
        });
    }

    private CrmUser verifyCrmUser(final GlobalCrmUserIndex idx, final String password) {
        return TaskRunner.runInTargetNamespace(idx.pmc(), new Callable<CrmUser>() {
            @Override
            public CrmUser call() {
                CrmUserCredential credentials = Persistence.service().retrieve(CrmUserCredential.class, idx.crmUser().getValue());
                if (credentials == null) {
                    throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
                }
                if (!credentials.enabled().isBooleanTrue()) {
                    return null;
                }

                if (ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(password, credentials.credential().getValue())) {
                    return Persistence.service().retrieve(CrmUser.class, idx.crmUser().getValue());
                } else {
                    return null;
                }
            }
        });
    }

    void createGlobalCrmUserIndex(final CrmUser user) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                GlobalCrmUserIndex idx = EntityFactory.create(GlobalCrmUserIndex.class);
                idx.pmc().set(pmc);
                idx.crmUser().setValue(user.getPrimaryKey());
                idx.email().setValue(user.email().getValue());
                Persistence.service().persist(idx);
                return null;
            }
        });
    }

    void updateGlobalCrmUserIndex(final CrmUser user) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                EntityQueryCriteria<GlobalCrmUserIndex> criteria = EntityQueryCriteria.create(GlobalCrmUserIndex.class);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.eq(criteria.proto().crmUser(), user.getPrimaryKey());
                GlobalCrmUserIndex idx = Persistence.service().retrieve(criteria);
                idx.email().setValue(user.email().getValue());
                Persistence.service().persist(idx);
                return null;
            }
        });
    }
}
