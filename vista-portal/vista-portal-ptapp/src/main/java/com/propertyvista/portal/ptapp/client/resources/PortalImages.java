/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface PortalImages extends ClientBundle {

    PortalImages INSTANCE = GWT.create(PortalImages.class);

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("add.png")
    ImageResource add();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("add_hover.png")
    ImageResource addHover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("del.png")
    ImageResource del();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("del_hover.png")
    ImageResource delHover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("empty.png")
    ImageResource hideRemoveRow();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("bg_body.gif")
    ImageResource bodyBackground();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("bg_body_2.gif")
    ImageResource body2Background();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step.png")
    ImageResource step();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_latest.png")
    ImageResource stepLatest();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_valid.png")
    ImageResource stepValid();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_invalid.png")
    ImageResource stepInvalid();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_pointer.png")
    ImageResource stepPointer();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_pointer_latest.png")
    ImageResource stepPointerLatest();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_pointer_valid.png")
    ImageResource stepPointerValid();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step_pointer_invalid.png")
    ImageResource stepPointerInvalid();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("btn.gif")
    ImageResource buttonBackground();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("clip.png")
    ImageResource clip();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("bulet.gif")
    ImageResource bulet();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("dont_worry.png")
    ImageResource dontWorry();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("requirements.png")
    ImageResource requirements();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("time.png")
    ImageResource time();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("exclamation.png")
    ImageResource exclamation();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("check.png")
    ImageResource check();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("check_hover.png")
    ImageResource checkHover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("warning_el.png")
    ImageResource warning();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("warning_el_hover.png")
    ImageResource warningHover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("user_message_info.png")
    ImageResource userMessageInfo();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("info.png")
    ImageResource info();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("info_orange.png")
    ImageResource infoOrange();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("info_red.png")
    ImageResource infoRed();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("pointer_menu.png")
    ImageResource pointer();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("pointer_expanded.png")
    ImageResource pointerExpanded();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("pointer_collapsed.png")
    ImageResource pointerCollapsed();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("qv.png")
    ImageResource qv();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("qv_hover.png")
    ImageResource qvHover();

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

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("bild.gif")
    ImageResource building();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("home.png")
    ImageResource floorplan();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("step2.png")
    ImageResource step2();

}
