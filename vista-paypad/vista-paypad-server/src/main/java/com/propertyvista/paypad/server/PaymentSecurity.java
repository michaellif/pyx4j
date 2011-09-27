/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-06-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.server;

import java.util.List;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.paypad.domain.InterfaceEntity;

public class PaymentSecurity {

    private final static Logger log = LoggerFactory.getLogger(PaymentSecurity.class);

    public static final String adminNamespace = "-paypad\u0010-";

    public static boolean enter(RequestMessage requestMessage) {
        try {
            AbstractAntiBot.assertLogin(requestMessage.getInterfaceEntity(), null);
        } catch (Throwable e) {
            return false;
        }
        InterfaceEntity user;
        try {
            NamespaceManager.setNamespace(PaymentSecurity.adminNamespace);

            EntityQueryCriteria<InterfaceEntity> criteria = EntityQueryCriteria.create(InterfaceEntity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().name(), requestMessage.getInterfaceEntity()));
            List<InterfaceEntity> users = Persistence.service().query(criteria);
            if (users.size() != 1) {
                log.debug("Invalid log-in attempt {} rs {}", requestMessage.getInterfaceEntity(), users.size());
                AbstractAntiBot.authenticationFailed(requestMessage.getInterfaceEntity());
                return false;
            }

            user = users.get(0);
            if (!user.enabled().isBooleanTrue()) {
                return false;
            }
            if (!checkPassword(requestMessage.getInterfaceEntityPassword(), user.credential().getValue())) {
                log.info("Invalid password for user {}", requestMessage.getInterfaceEntity());
                AbstractAntiBot.authenticationFailed(requestMessage.getInterfaceEntity());
                return false;
            }
        } finally {
            NamespaceManager.remove();
        }

        NamespaceManager.setNamespace(user.namespace().getValue());
        return true;
    }

    public static String encryptPassword(String userPassword) {
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        return passwordEncryptor.encryptPassword(userPassword);
    }

    public static boolean checkPassword(String inputPassword, String encryptedPassword) {
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        return passwordEncryptor.checkPassword(inputPassword, encryptedPassword);
    }
}
