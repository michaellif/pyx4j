/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.statusviewers;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityViewer;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;

/** this is a class that supposed to implement 'polymorphic' tenant insurance status viewer */
public class TenantInsuranceStatusViewer extends CEntityViewer<TenantInsuranceStatusDTO> {

    @Override
    public IsWidget createContent(TenantInsuranceStatusDTO tenantInsuranceStatus) {
        if (tenantInsuranceStatus instanceof NoInsuranceTenantInsuranceStatusDTO) {
            return new NoTenantInsuranceStatusViewer().createContent((NoInsuranceTenantInsuranceStatusDTO) tenantInsuranceStatus);
        } else {
            throw new Error("A viewer for current insurance status was not found!");
        }
    }
}
