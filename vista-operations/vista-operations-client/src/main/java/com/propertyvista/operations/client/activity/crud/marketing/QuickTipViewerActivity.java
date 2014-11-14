/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.marketing;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.marketing.PortalResidentMarketingTip;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.marketing.QuickTipViewerView;
import com.propertyvista.operations.rpc.services.QuickTipCrudService;

public class QuickTipViewerActivity extends AbstractViewerActivity<PortalResidentMarketingTip> implements QuickTipViewerView.Presenter {

    public QuickTipViewerActivity(CrudAppPlace place) {
        super(PortalResidentMarketingTip.class, place, OperationsSite.getViewFactory().getView(QuickTipViewerView.class), GWT
                .<QuickTipCrudService> create(QuickTipCrudService.class));
    }
}
