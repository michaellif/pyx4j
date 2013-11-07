/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.financial.autopay;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ui.financial.autopay.AutoPayView;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.AutoPayWizardService;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;

public class AutoPayActivity extends AbstractEditorActivity<AutoPayDTO> implements AutoPayView.Presenter {

    public AutoPayActivity(AppPlace place) {
        super(AutoPayView.class, GWT.<AutoPayWizardService> create(AutoPayWizardService.class), place);
    }
}
