/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

public class CommunicationEndpointSelectorAddDialog extends Dialog implements OkCancelOption {

    private final SelectRecipientsDialogForm selectForm;

    private final CommunicationEndpointSelector parent;

    public CommunicationEndpointSelectorAddDialog(CommunicationEndpointSelector parent) {
        super("Select recipients");
        this.setDialogOptions(this);
        this.parent = parent;
        selectForm = new SelectRecipientsDialogForm();
        setDialogPixelWidth(1000);
        setBody(selectForm);

    }

    public CommunicationEndpointSelectorAddDialog() {
        this(null);

    }

    @Override
    public boolean onClickCancel() {
        this.hide(true);
        return true;
    }

    @Override
    public boolean onClickOk() {
        this.hide(true);
        return true;
    }
}
