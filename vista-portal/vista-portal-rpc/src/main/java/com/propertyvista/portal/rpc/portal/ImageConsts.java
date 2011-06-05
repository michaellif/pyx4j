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

import com.pyx4j.site.shared.Dimension;

//Note: In future we may decide to move this to configurations of  each property management company....
public interface ImageConsts {

    public static enum ThumbnailSize {
        small, medum, large
    }

    public static final Dimension BUILDING_SMALL = new Dimension(70, 70);

    public static final Dimension BUILDING_MEDUM = new Dimension(150, 100);

    public static final Dimension BUILDING_LARGE = new Dimension(300, 200);

}
