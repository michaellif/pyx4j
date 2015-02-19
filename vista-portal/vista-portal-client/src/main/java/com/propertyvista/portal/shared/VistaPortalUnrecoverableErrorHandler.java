/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2015
 * @author vlads
 */
package com.propertyvista.portal.shared;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;

public class VistaPortalUnrecoverableErrorHandler extends VistaUnrecoverableErrorHandler {

    private static final I18n i18n = I18n.get(VistaPortalUnrecoverableErrorHandler.class);

    @Override
    protected String defaultErrorMessageText() {
        return i18n
                .tr("Oops. We are experiencing technical difficulties. Please log out and try again. If the problem persists, please contact your management company.\n");
    }

}
