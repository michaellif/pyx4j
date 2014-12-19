/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-25
 * @author ArtyomB
 */
package com.propertyvista.biz.occupancy;

import java.io.Serializable;

public class OccupancyOperationException extends Exception implements Serializable {

    private static final long serialVersionUID = 7338078149719985639L;

    String message;

    public OccupancyOperationException() {
        this("");
    }

    public OccupancyOperationException(String message) {
        super(message);
    }

}
