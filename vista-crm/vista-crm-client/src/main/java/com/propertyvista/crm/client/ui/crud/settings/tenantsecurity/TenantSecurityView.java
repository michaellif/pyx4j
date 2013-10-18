/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.tenantsecurity;

import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.domain.tenant.access.PortalAccessSecutiryCodeReportType;

public interface TenantSecurityView extends IPane {

    interface Presenter extends IPane.Presenter {

        void generatePortalSecurityCodes(PortalAccessSecutiryCodeReportType type);

    }

    void setPresenter(Presenter presenter);

}
