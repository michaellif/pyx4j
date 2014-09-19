/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.operations.domain.imports.OapiConversion;
import com.propertyvista.operations.rpc.dto.OapiConversionDTO;
import com.propertyvista.operations.rpc.services.OapiCrudService;

public class OapiCrudServiceImpl extends AbstractCrudServiceDtoImpl<OapiConversion, OapiConversionDTO> implements OapiCrudService {

    public OapiCrudServiceImpl() {
        super(OapiConversion.class, OapiConversionDTO.class);
    }

    // TODO Override CRUD Methods here

    @Override
    protected void enhanceListRetrieved(OapiConversion bo, OapiConversionDTO to) {
//        super.enhanceListRetrieved(bo, to);

        to.filesNumber().setValue(bo.conversionFiles().size());
    }
}
