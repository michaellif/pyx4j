/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class HandledErrorAsyncCallback<E> implements AsyncCallback<E> {

    private static final I18n i18n = I18n.get(HandledErrorAsyncCallback.class);

    @Override
    public void onFailure(Throwable caught) {
        if (caught instanceof UserRuntimeException) {
            MessageDialog.warn(i18n.tr("Error"), caught.getMessage());
        } else {
            throw new UnrecoverableClientError(caught);
        }
    }

}
