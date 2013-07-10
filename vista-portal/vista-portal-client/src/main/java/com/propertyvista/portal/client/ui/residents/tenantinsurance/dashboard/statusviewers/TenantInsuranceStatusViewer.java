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
package com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CViewer;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;

/** this is a class that supposed to implement 'polymorphic' tenant insurance status viewer */
public class TenantInsuranceStatusViewer extends CViewer<TenantInsuranceStatusDTO> {

    public enum Styles {
        TenantInsuranceWarningText, TenantInsuranceAnchor;
    }

    @Override
    public IsWidget createContent(TenantInsuranceStatusDTO tenantInsuranceStatus) {
        if (tenantInsuranceStatus == null) {
            return null;
        }

        if (tenantInsuranceStatus instanceof NoInsuranceTenantInsuranceStatusDTO) {
            return new NoTenantInsuranceStatusViewer().createContent((NoInsuranceTenantInsuranceStatusDTO) tenantInsuranceStatus);

        } else if (tenantInsuranceStatus instanceof TenantSureTenantInsuranceStatusShortDTO) {
            return new TenantSureInsuranceStatusViewer().createContent((TenantSureTenantInsuranceStatusShortDTO) tenantInsuranceStatus);

        } else if (tenantInsuranceStatus instanceof OtherProviderTenantInsuranceStatusDTO) {
            return new OtherProviderTenantInsuranceStatusViewer().createContent((OtherProviderTenantInsuranceStatusDTO) tenantInsuranceStatus);

        } else {
            throw new Error("A viewer for current insurance status was not found!");
        }
    }
}
