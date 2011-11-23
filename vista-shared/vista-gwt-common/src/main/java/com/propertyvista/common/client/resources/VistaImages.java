/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ImageFactory.FormsImageBundle;
import com.pyx4j.widgets.client.images.WidgetsImages;

public interface VistaImages extends FormsImageBundle, EntityFolderImages, WidgetsImages {

    VistaImages INSTANCE = GWT.create(VistaImages.class);

    @Override
    @Source("date.png")
    ImageResource triggerBlueUp();

    @Override
    @Source("date.png")
    ImageResource triggerBlueOver();

    @Override
    @Source("date.png")
    ImageResource triggerBlueDown();

    @Override
    @Source("date.png")
    ImageResource triggerBlueDisabled();

    @Source("clip.png")
    ImageResource clip();

    @Override
    @Source("qv.png")
    ImageResource formTooltipInfo();

    @Source("logo.png")
    ImageResource logo();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("payment-master.gif")
    ImageResource paymentMC();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("payment-visa.gif")
    ImageResource paymentVISA();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("payment-amex.gif")
    ImageResource paymentAMEX();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("payment-discover.gif")
    ImageResource paymentDiscover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("payment-interact.gif")
    ImageResource paymentInterac();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("payment-ach.gif")
    ImageResource paymentACH();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("cheque-guide.jpg")
    ImageResource chequeGuide();
}
