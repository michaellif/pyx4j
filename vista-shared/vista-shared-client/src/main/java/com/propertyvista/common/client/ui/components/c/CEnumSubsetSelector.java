/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import java.util.EnumSet;
import java.util.Set;

import com.pyx4j.commons.IFormatter;

import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;

public class CEnumSubsetSelector<E extends Enum<E>> extends CSubsetSelector<E> {

    public CEnumSubsetSelector(Set<E> options, Layout layout) {
        super(layout, new IFormatter<E>() {
            @Override
            public String format(E value) {
                if (value != null) {
                    return value.toString();
                } else {
                    return "";
                }
            }

        }, options);
    }

    public CEnumSubsetSelector(Class<E> elementType, Layout layout) {
        this(EnumSet.allOf(elementType), layout);
    }

}
