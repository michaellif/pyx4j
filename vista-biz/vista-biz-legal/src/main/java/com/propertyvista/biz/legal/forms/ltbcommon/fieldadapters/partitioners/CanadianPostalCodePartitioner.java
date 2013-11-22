/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.partitioners;

import com.propertyvista.biz.legal.forms.framework.mapping.Partitioner;

public class CanadianPostalCodePartitioner implements Partitioner {

    @Override
    public String getPart(String value, int partIndex) {
        String part = null;
        switch (partIndex) {
        case 0:
        case 1:
            part = value.split(" ")[partIndex];
            break;
        default:
            part = "";
            break;
        }
        return part;
    }

}
