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

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.NoInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.OtherProviderInsuranceStatusDTO;

/** this is a class that supposed to implement 'polymorphic' tenant insurance status viewer */
public class TenantInsuranceStatusViewer extends CViewer<InsuranceStatusDTO> {

    public enum Styles {
        TenantInsuranceWarningText, TenantInsuranceAnchor;
    }

    @Override
    public IsWidget createContent(InsuranceStatusDTO tenantInsuranceStatus) {
        if (tenantInsuranceStatus == null) {
            return null;
        }

        if (tenantInsuranceStatus instanceof NoInsuranceStatusDTO) {
            return new NoTenantInsuranceStatusViewer().createContent((NoInsuranceStatusDTO) tenantInsuranceStatus);

        } else if (tenantInsuranceStatus instanceof TenantSureTenantInsuranceStatusDTO) {
            return new TenantSureInsuranceStatusViewer().createContent((TenantSureTenantInsuranceStatusDTO) tenantInsuranceStatus);

        } else if (tenantInsuranceStatus instanceof OtherProviderInsuranceStatusDTO) {
            return new OtherProviderTenantInsuranceStatusViewer().createContent((OtherProviderInsuranceStatusDTO) tenantInsuranceStatus);

        } else {
            throw new Error("A viewer for current insurance status was not found!");
        }
    }
}
