/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal;

import java.io.Serializable;

import com.pyx4j.site.shared.Dimension;

//Note: In future we may decide to move this to configurations of  each property management company....
public interface ImageConsts {

    public static enum ThumbnailSize implements Serializable {
        xsmall, small, medium, large, xlarge
    }

    public static enum ThumbnailType {
        jpg, png
    }

    public final ThumbnailType THUMBNAIL_TYPE = ThumbnailType.png;

    public static enum ImageTarget implements Serializable {

        Building,

        Floorplan
    }

    public static final Dimension BUILDING_XSMALL = new Dimension(57, 41);

    public static final Dimension BUILDING_SMALL = new Dimension(70, 50);

    public static final Dimension BUILDING_MEDIUM = new Dimension(140, 93);

    public static final Dimension BUILDING_LARGE = new Dimension(318, 212);

    public static final Dimension FLOORPLAN_SMALL = new Dimension(57, 40);

    public static final Dimension FLOORPLAN_MEDIUM = new Dimension(80, 80);

    public static final Dimension FLOORPLAN_LARGE = new Dimension(318, 321);

    // Used by rentstarlight.com
    public static final Dimension FLOORPLAN_XLARGE = new Dimension(500, 505);

}
