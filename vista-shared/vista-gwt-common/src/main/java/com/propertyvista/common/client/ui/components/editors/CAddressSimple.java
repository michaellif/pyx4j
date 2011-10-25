/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.contact.AddressSimple;

public class CAddressSimple extends CAddressSimpleImpl<AddressSimple> {

    public CAddressSimple() {
        super(AddressSimple.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = internalCreateContent();
        main.setWidth("100%");
        return main;
    }
}
