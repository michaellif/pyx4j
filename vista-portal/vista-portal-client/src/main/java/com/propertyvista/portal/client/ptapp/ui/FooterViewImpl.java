/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class FooterViewImpl extends SimplePanel implements FooterView {

    public FooterViewImpl() {
        HTML labael = new HTML("&copy;2011- All Rights Reserved.");
        setWidget(labael);
        labael.getElement().getStyle().setProperty("borderTop", "dotted 1px #A7A8AA");
        getElement().getStyle().setPaddingTop(10, Unit.PX);

    }

}
