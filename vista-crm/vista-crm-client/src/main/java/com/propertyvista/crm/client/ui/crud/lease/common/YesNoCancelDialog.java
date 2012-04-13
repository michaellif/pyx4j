/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-11
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.YesNoCancelOption;

public abstract class YesNoCancelDialog extends Dialog implements YesNoCancelOption {

    public YesNoCancelDialog(String caption) {
        super(caption);
        setDialogOptions(this);
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }
}