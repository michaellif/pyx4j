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

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ImageFactory.FormsImageBundle;

public interface FormImageBundle extends FormsImageBundle, EntityFolderImages {

    FormImageBundle INSTANCE = GWT.create(FormImageBundle.class);

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

    @Source("add.png")
    ImageResource addRow();

    @Source("add_hover.png")
    ImageResource addRowHover();

    @Source("del.png")
    ImageResource delRow();

    @Source("del_hover.png")
    ImageResource delRowHover();

    @Override
    @Source("qv.png")
    ImageResource formTooltipInfo();
}
