/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.login;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.web.client.themes.LandingPagesTheme;

public class LoginImageDecoration extends Composite {

    public LoginImageDecoration(ImageResource imageResource, String caption) {

        FlowPanel panel = new FlowPanel();
        panel.setStyleName(LandingPagesTheme.StyleName.LoginImageDecoration.name());
        Image image = new Image(imageResource);
        image.setStyleName(LandingPagesTheme.StyleName.LoginImageDecorationImage.name());
        panel.add(image);

        Label label = new Label();
        label.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        label.addStyleName(LandingPagesTheme.StyleName.LoginImageDecorationLabel.name());
        label.setText(caption);
        panel.add(label);

        initWidget(panel);

    }
}
