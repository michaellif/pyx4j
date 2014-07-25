/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.security.CrmRole;

public class MessageCategoryPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        createCategory(MessageGroupCategory.TenantOriginated, "Tenant Originated Communication", CrmRolesPreloader.getDefaultRole());
        createCategory(MessageGroupCategory.LandlordOriginated, "Landlord Originated Communication", CrmRolesPreloader.getDefaultRole());
        createCategory(MessageGroupCategory.VendorOriginated, "Vendor Originated Communication", CrmRolesPreloader.getDefaultRole());
        createCategory(MessageGroupCategory.Custom, "General Communication", CrmRolesPreloader.getDefaultRole());
        return null;
    }

    private void createCategory(MessageGroupCategory category, String topic, CrmRole... defaultRole) {
        MessageCategory mg = EntityFactory.create(MessageCategory.class);
        mg.category().setValue(category);
        mg.topic().setValue(topic);
        if (defaultRole != null && defaultRole.length > 0) {
            for (int i = 0; i < defaultRole.length; ++i)
                mg.roles().add(defaultRole[i]);
        }
        PersistenceServicesFactory.getPersistenceService().persist(mg);
    }

    @Override
    public String delete() {
        return null;
    }
}
