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

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.commons.IFormat;
import com.pyx4j.forms.client.ui.CFocusComponent;

import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;

public class CSubsetSelector<OPTION_TYPE> extends CFocusComponent<Set<OPTION_TYPE>, NSubsetSelector<OPTION_TYPE>> {

    private final Layout layout;

    private final IFormat<OPTION_TYPE> format;

    private final Set<OPTION_TYPE> options;

    public CSubsetSelector(SubsetSelector.Layout layout, IFormat<OPTION_TYPE> format, Set<OPTION_TYPE> options) {
        this.layout = layout;
        this.format = format;
        this.options = options;
        setNativeWidget(new NSubsetSelector<OPTION_TYPE>(this, this.format));
    }

    public SubsetSelector.Layout getLayout() {
        return layout;
    }

    public Set<OPTION_TYPE> getOptions() {
        return new HashSet<OPTION_TYPE>(options);
    }

}
