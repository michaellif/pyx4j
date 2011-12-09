/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 7, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

class GadgetCategoryCell extends AbstractCell<GadgetCategoryWrapper> {

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, GadgetCategoryWrapper value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.appendHtmlConstant(value.toString());
        }
    }
}