/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.blob;

import java.io.Serializable;

public class ResampleParams {

    boolean crop = false;

    //Do not resize already small images
    boolean enlarge = false;

    public int clipBorders = 0;

    public int clipThumpnailBorders = 1;

    public static enum UnsharpenMask implements Serializable {
        None, Soft, Normal
    }

    public UnsharpenMask unsharpenMask = UnsharpenMask.Normal;

}
