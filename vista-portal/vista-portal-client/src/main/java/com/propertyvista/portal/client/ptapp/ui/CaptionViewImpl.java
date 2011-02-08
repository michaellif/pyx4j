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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class CaptionViewImpl extends SimplePanel implements CaptionView {

    private final Label captionLabel;

    public CaptionViewImpl() {
        captionLabel = new Label();
        captionLabel.setSize("300px", "40px");
        captionLabel.getElement().getStyle().setFontSize(1.4, Unit.EM);
        captionLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        setWidget(captionLabel);

        getElement().getStyle().setProperty("borderBottom", "solid 1px #ccc");
        getElement().getStyle().setMargin(10, Unit.PX);

    }

    @Override
    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

}
