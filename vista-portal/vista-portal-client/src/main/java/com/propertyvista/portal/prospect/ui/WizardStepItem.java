/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.shared.ui.MenuItem;

public class WizardStepItem extends MenuItem<StepIndexLabel> {

    public static enum StepStatus {
        notComplete, complete, invalid, current
    }

    public WizardStepItem(String title, Command command, int index, StepStatus status) {
        super(title, command, new StepIndexLabel(String.valueOf(index + 1)), ThemeColor.contrast2);
        getIcon().setStatus(status);
        setSelected(StepStatus.current.equals(status));
    }

}