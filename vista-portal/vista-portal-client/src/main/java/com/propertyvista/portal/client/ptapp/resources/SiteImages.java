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
package com.propertyvista.portal.client.ptapp.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface SiteImages extends ClientBundle {

    SiteImages INSTANCE = GWT.create(SiteImages.class);

    @Source("list_add.png")
    ImageResource addRow();

    @Source("list_remove.png")
    ImageResource removeRow();

    @Source("list_empty.png")
    ImageResource hideRemoveRow();

    @Source("bg_body.gif")
    ImageResource bodyBackground();

    @Source("bg_body_2.gif")
    ImageResource body2Background();

    @Source("bg_step_block.gif")
    ImageResource stepBlockBackgroun();

    @Source("step_pointer.png")
    ImageResource stepPointer();

    @Source("btn.gif")
    ImageResource buttonBackground();

    @Source("bulet.gif")
    ImageResource bulet();

    @Source("dont_worry.png")
    ImageResource dontWorry();

    @Source("requirements.png")
    ImageResource requirements();

    @Source("time.png")
    ImageResource time();

    @Source("exclamation.png")
    ImageResource exclamation();

    @Source("check.png")
    ImageResource check();

    @Source("user_message_info.png")
    ImageResource userMessageInfo();

    @Source("qv.png")
    ImageResource qv();

}
