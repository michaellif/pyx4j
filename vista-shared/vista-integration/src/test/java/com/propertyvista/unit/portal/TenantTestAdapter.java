/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import java.util.List;

import com.propertvista.generator.gdo.TenantSummaryGDO;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;
import com.propertyvista.server.common.util.TenantConverter;

public class TenantTestAdapter {

    public static TenantInApplicationListDTO getTenantListEditorDTO(List<TenantSummaryGDO> tenant) {
        TenantInApplicationListDTO dto = EntityFactory.create(TenantInApplicationListDTO.class);
        for (TenantSummaryGDO tenantSummary : tenant) {
            dto.tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantSummary.tenantInLease()));
        }
        return dto;
    }
}
