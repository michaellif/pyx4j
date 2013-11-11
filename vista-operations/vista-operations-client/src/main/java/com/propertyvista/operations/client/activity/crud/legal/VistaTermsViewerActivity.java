/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 21, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.legal;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.legal.VistaTermsViewerView;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.rpc.services.VistaTermsCrudService;

public class VistaTermsViewerActivity extends AbstractViewerActivity<VistaTerms> implements VistaTermsViewerView.Presenter {

    public VistaTermsViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(VistaTermsViewerView.class), GWT.<VistaTermsCrudService> create(VistaTermsCrudService.class));
    }

}
