/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import java.util.Collections;
import java.util.List;

public class ObjectSelectionState<E> {

    private final List<E> options;

    private final E selected;

    public ObjectSelectionState(List<E> options, E selected) {
        this.options = options;
        this.selected = selected;
    }

    public final List<E> getOptions() {
        return Collections.unmodifiableList(this.options);
    }

    public final E getSelectedOption() {
        return selected;
    }

    public final ObjectSelectionState<E> updatedSelection(E newSelected) {
        return new ObjectSelectionState<E>(getOptions(), newSelected);
    }

}