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
package com.propertyvista.field.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

import com.propertyvista.common.client.resources.VistaImages;

public interface FieldImages extends VistaImages {

    FieldImages INSTANCE = GWT.create(FieldImages.class);

    @Source("blank.gif")
    ImageResource blank();

    @Source("menu.png")
    ImageResource menu();

    @Source("search.png")
    ImageResource search();

    @Source("sort.png")
    ImageResource sort();

    @Source("back.png")
    ImageResource back();

    @Source("alerts.png")
    ImageResource alerts();
}
