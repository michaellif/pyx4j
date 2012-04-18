/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class CustomerFacadeImpl implements CustomerFacade {

    private static final I18n i18n = I18n.get(CustomerFacadeImpl.class);

    @Override
    public void persistCustomer(Customer customer) {
        if (customer.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(customer);
        }
        Persistence.service().retrieve(customer.user());
        customer.user().name().setValue(customer.person().name().getStringView());
        customer.user().email().setValue(customer.person().email().getValue());
        if (customer.user().getPrimaryKey() != null) {
            Persistence.service().merge(customer.user());
        } else {
            if (customer.person().email().isNull()) {
                throw new UnRecoverableRuntimeException(i18n.tr("Can't create application user for tenant  {0} without e-mail address", customer.person()
                        .name().getStringView()));
            }
            Persistence.service().persist(customer.user());

            TenantUserCredential credential = EntityFactory.create(TenantUserCredential.class);
            credential.setPrimaryKey(customer.user().getPrimaryKey());
            credential.user().set(customer.user());
            if (ApplicationMode.isDevelopment()) {
                credential.credential().setValue(PasswordEncryptor.encryptPassword(customer.user().email().getValue()));
            }
            credential.enabled().setValue(Boolean.TRUE);
            Persistence.service().persist(credential);
        }

        Persistence.service().merge(customer);
    }

}
