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

import static com.propertyvista.domain.security.VistaCrmBehavior.GuarantorAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.GuarantorBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.GuarantorFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.services.customer.ac.GuarantorChangePassword;
import com.propertyvista.dto.GuarantorDTO;

class VistaCrmGuarantorAccessControlList extends UIAclBuilder {

    VistaCrmGuarantorAccessControlList() {
        { // 
            List<Class<? extends IEntity>> entities = entities(GuarantorDTO.class);

            grant(GuarantorBasic, entities, READ);
            grant(GuarantorAdvanced, entities, READ);
            grant(GuarantorFull, entities, READ | UPDATE);
        }

        // Actions:
        grant(GuarantorBasic, new ActionPermission(GuarantorChangePassword.class));
        grant(GuarantorAdvanced, new ActionPermission(GuarantorChangePassword.class));
        grant(GuarantorFull, new ActionPermission(GuarantorChangePassword.class));
    }
}
