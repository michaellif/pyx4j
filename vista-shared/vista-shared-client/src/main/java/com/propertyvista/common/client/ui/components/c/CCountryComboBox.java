/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 26, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.forms.client.ui.CComboBox;

import com.propertyvista.domain.ref.ISOCountry;

public class CCountryComboBox extends CComboBox<ISOCountry> {

    private final List<ISOCountry> top;

    public CCountryComboBox() {
        this((ISOCountry[]) null);
    }

    public CCountryComboBox(ISOCountry... topList) {
        super();
        top = Arrays.asList(topList);
        setOptions(EnumSet.allOf(ISOCountry.class));
    }

    @Override
    public void setOptions(Collection<ISOCountry> opt) {
        ArrayList<ISOCountry> sorted = new ArrayList<>(opt);
        Collections.sort(sorted, new Comparator<ISOCountry>() {
            @Override
            public int compare(ISOCountry o1, ISOCountry o2) {
                if (o1 == null || o2 == null) {
                    return -1;
                } else if (top.contains(o1) && !top.contains(o2)) {
                    return -1;
                } else if (top.contains(o2) && !top.contains(o1)) {
                    return 1;
                } else {
                    return o1.compareTo(o2);
                }
            }
        });
        super.setOptions(sorted);
    }
}
