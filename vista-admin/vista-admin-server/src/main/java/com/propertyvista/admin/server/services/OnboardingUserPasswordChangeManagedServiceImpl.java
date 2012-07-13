/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.services.OnboardingUserPasswordChangeManagedService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaManagedPasswordChangeServiceImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class OnboardingUserPasswordChangeManagedServiceImpl extends VistaManagedPasswordChangeServiceImpl<OnboardingUserCredential> implements
        OnboardingUserPasswordChangeManagedService {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordChangeManagedServiceImpl.class);

    private static final I18n i18n = I18n.get(VistaManagedPasswordChangeServiceImpl.class);

    public OnboardingUserPasswordChangeManagedServiceImpl() {
        super(OnboardingUserCredential.class);
    }

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        // TODO USe CRM use if exists
        if (VistaTODO.VISTA_1588) {
            log.warn("TODO - implement CRM User/OnboardingUser synchronization");
        }

        if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(request.userPk().getValue())) {
            throw new SecurityViolationException(i18n.tr("Permission denied"));
        }

        OnboardingUserCredential cr = Persistence.service().retrieve(credentialClass, request.userPk().getValue());

        if (cr.pmc().getPrimaryKey() != null) {
            Pmc pmc = Persistence.service().retrieve(Pmc.class, cr.pmc().getPrimaryKey());

            if (pmc.status().getValue() != PmcStatus.Created) {
                String curNameSpace = NamespaceManager.getNamespace();

                try {
                    NamespaceManager.setNamespace(pmc.namespace().getValue());

                    EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), cr.getPrimaryKey()));
                    CrmUserCredential credential = Persistence.service().retrieve(crmUCrt);

                    credential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
                    if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
                        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
                    }
                    Persistence.service().persist(credential);
                    Persistence.service().commit();

                } finally {
                    NamespaceManager.setNamespace(curNameSpace);
                }
            }
        }

        super.changePassword(callback, request);
    }

}
