/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 13, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.shared.adapters.CustomerSignatureAdapter;

public class CustomerSignatureAdapterImpl implements CustomerSignatureAdapter {

    @Override
    public void onBeforeUpdate(CustomerSignature origEntity, CustomerSignature newEntity) {
        if (newEntity.agree().getValue(false)) {
            if ((origEntity == null) || !origEntity.agree().getValue(false)) {
                Validate.isEquals(CustomerUser.class, VistaContext.getCurrentUser().getValueClass(), "Only Customer can signe CustomerSignature");
                newEntity.signingUser().set(VistaContext.getCurrentUser());
                newEntity.signDate().setValue(SystemDateManager.getDate());
                newEntity.ipAddress().setValue(Context.getRequestRemoteAddr());
            }
        } else {
            // Erase signature since it is set to non signed
            if (newEntity.hasValues()) {
                newEntity.signingUser().set(null);
                newEntity.signDate().setValue(null);
                newEntity.ipAddress().setValue(null);
            }
        }

    }
}
