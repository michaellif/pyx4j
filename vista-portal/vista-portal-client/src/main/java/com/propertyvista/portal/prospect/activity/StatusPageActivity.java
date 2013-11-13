/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.ui.StatusPageView;
import com.propertyvista.portal.prospect.ui.StatusPageView.StatusPagePresenter;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusCrudService;
import com.propertyvista.portal.rpc.portal.web.dto.application.ApplicationStatusDTO;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;

public class StatusPageActivity extends AbstractEditorActivity<ApplicationStatusDTO> implements StatusPagePresenter {

    public StatusPageActivity(AppPlace place) {
        super(StatusPageView.class, GWT.<ApplicationStatusCrudService> create(ApplicationStatusCrudService.class), place);
    }

}
