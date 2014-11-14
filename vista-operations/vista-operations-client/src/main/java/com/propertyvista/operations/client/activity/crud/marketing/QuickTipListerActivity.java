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

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.marketing.PortalResidentMarketingTip;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.marketing.QuickTipListerView;

public class QuickTipListerActivity extends AbstractPrimeListerActivity<PortalResidentMarketingTip> {

    public QuickTipListerActivity(AppPlace place) {
        super(PortalResidentMarketingTip.class, place, OperationsSite.getViewFactory().getView(QuickTipListerView.class));
    }
}
