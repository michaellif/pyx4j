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
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.contact.Address;

public class CAddress extends CAddressFull<Address> {

    public CAddress() {
        super(Address.class);
    }

    public CAddress(boolean twoColumns) {
        super(Address.class, twoColumns);
    }

    public CAddress(boolean twoColumns, boolean showUnit) {
        super(Address.class, twoColumns, showUnit);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = internalCreateContent();

        main.setWidth("100%");
        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }
}
