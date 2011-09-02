/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 2, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

public class AttributeClassModifier extends AttributeModifier {
    private static final long serialVersionUID = 1L;

    private final String find;

    public AttributeClassModifier(String find, String replace) {
        super("class", true, new Model<String>(replace));
        this.find = find;
    }

    @Override
    protected String newValue(final String curVal, final String repVal) {
        if (curVal == null || curVal.equals("")) {
            return repVal;
        }
        StringBuffer newVal = new StringBuffer();
        String[] valArr = curVal.split(" ");
        for (int i = 0; i < valArr.length; i++) {
            newVal.append(i > 0 ? " " : "");
            if (valArr[i].equals(find)) {
                newVal.append(repVal == null ? "" : repVal);
            } else {
                newVal.append(valArr[i]);
            }
        }
        return newVal.toString();
    }
}
