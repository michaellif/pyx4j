/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-26
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.gadgets;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.site.gadgets.HomePageGadget;

public class HomePageGadgetEditorImpl extends CrmEditorViewImplBase<HomePageGadget> implements HomePageGadgetEditor {
    public HomePageGadgetEditorImpl() {
        setForm(new HomePageGadgetForm(this));
    }
}