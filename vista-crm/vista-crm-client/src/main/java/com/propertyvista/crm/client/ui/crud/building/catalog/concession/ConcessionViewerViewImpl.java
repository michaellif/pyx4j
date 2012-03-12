/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.concession;

import com.google.gwt.core.client.GWT;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.version.ConcessionVersionService;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionViewerViewImpl extends CrmViewerViewImplBase<Concession> implements ConcessionViewerView {

    public ConcessionViewerViewImpl() {
        super(CrmSiteMap.Properties.Concession.class, new ConcessionEditorForm(true));
        enableVersioning(Concession.ConcessionV.class, GWT.<ConcessionVersionService> create(ConcessionVersionService.class));
    }
}