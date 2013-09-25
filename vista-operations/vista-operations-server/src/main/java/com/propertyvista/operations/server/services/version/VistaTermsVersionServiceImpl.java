/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-28
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.services.version;

import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.rpc.services.version.VistaTermsVersionService;
import com.propertyvista.server.versioning.AbstractVistaVersionDataListServiceImpl;

public class VistaTermsVersionServiceImpl extends AbstractVistaVersionDataListServiceImpl<VistaTerms.VistaTermsV> implements VistaTermsVersionService {

    public VistaTermsVersionServiceImpl() {
        super(VistaTerms.VistaTermsV.class, OperationsUser.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }
}