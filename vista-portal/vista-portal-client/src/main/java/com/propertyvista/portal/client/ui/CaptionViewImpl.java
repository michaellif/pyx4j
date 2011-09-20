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
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CaptionViewImpl extends SimplePanel implements CaptionView {

    private final Label captionLabel;

    public CaptionViewImpl() {
        captionLabel = new Label();
        setWidget(captionLabel);
    }

    @Override
    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

}
