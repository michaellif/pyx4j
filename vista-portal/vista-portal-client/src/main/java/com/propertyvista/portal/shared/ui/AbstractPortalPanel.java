/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 6, 2013
 * @author michaellif
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.gwt.commons.layout.LayoutType;

public abstract class AbstractPortalPanel extends SimplePanel {

    public AbstractPortalPanel() {

        CssVariable.setVariable(getElement(), FieldDecorator.CSS_VAR_FIELD_DECORATOR_LABEL_POSITION_LAYOUT_TYPE, LayoutType.tabletLandscape.name());

    }

}
