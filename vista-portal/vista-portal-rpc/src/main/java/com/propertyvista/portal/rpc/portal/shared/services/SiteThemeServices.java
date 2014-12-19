/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 2, 2011
 * @author michaellif
 */
package com.propertyvista.portal.rpc.portal.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.shared.i18n.CompiledLocale;

public interface SiteThemeServices extends IService {

    public void retrieveSiteDescriptor(AsyncCallback<SiteDefinitionsDTO> callback, VistaApplication application, CompiledLocale locale);

}
