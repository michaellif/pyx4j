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
package com.propertyvista.paypad.server.ci.preloader;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.paypad.domain.InterfaceEntity;
import com.propertyvista.paypad.server.PaymentSecurity;

public class PreloadInterfaceEntities extends AbstractDataPreloader {

    @Override
    public String create() {
        NamespaceManager.setNamespace(PaymentSecurity.adminNamespace);

        InterfaceEntity credential = EntityFactory.create(InterfaceEntity.class);

        credential.name().setValue("PaymentProcessor1");
        credential.credential().setValue(PaymentSecurity.encryptPassword("top-secret"));
        credential.enabled().setValue(Boolean.TRUE);

        credential.namespace().setValue("t-yardi");
        PersistenceServicesFactory.getPersistenceService().persist(credential);

        return "Created " + 1 + " InterfaceEntities";
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(InterfaceEntity.class);
        } else {
            return "This is production";
        }
    }

}
