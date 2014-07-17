/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 17, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.TenantAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantFinancial;
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.services.customer.ac.TenantChangePassword;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;

public class VistaCrmTenantAccessControlList extends UIAclBuilder {

    public VistaCrmTenantAccessControlList() {
        { // 
            List<Class<? extends IEntity>> entities = entities(TenantDTO.class);

            grant(TenantBasic, entities, READ);
            grant(TenantAdvanced, entities, READ);
            grant(TenantFull, entities, ALL);
            grant(TenantFinancial, entities, READ | UPDATE);
        }

        grant(TenantBasic, TenantPortalAccessInformationDTO.class, READ);
        grant(TenantAdvanced, TenantPortalAccessInformationDTO.class, READ);
        grant(TenantFull, TenantPortalAccessInformationDTO.class, READ);

        // Actions:
        grant(TenantBasic, new ActionPermission(TenantChangePassword.class));
        grant(TenantAdvanced, new ActionPermission(TenantChangePassword.class));
        grant(TenantFull, new ActionPermission(TenantChangePassword.class));
        grant(TenantFinancial, new ActionPermission(TenantChangePassword.class));
    }
}
