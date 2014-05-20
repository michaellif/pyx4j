/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.FormFieldDecoratorOptions;

public class PortalFormPanel extends FormPanel {

    public PortalFormPanel(CForm<?> parent) {
        super(parent);
    }

    @Override
    protected FormFieldDecoratorOptions createFieldDecoratorOptions() {
        return new PortalFieldDecoratorOptions();
    }

    public class PortalFieldDecoratorOptions extends FormFieldDecoratorOptions {

        public PortalFieldDecoratorOptions() {
            super();
            labelWidth(220);
        }

    }
}
