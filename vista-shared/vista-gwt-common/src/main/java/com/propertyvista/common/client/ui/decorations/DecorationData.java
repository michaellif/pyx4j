/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.ui.decorations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

public class DecorationData {

    public static enum ShowMandatory {
        Mandatory, Optional, None
    }

    public boolean editable = true;

    public boolean readOnlyMode = false;

    public double labelWidth = 12;

    public Unit labelUnit = Unit.EM;

    public String labelStyleName;

    public HorizontalAlignmentConstant labelAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

    public VerticalAlign labelVerticalAlignment = VerticalAlign.BASELINE;

    public String componentCaption = null;

    public double componentWidth = 10;

    public Unit componentUnit = Unit.EM;

    public HorizontalAlignmentConstant componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

    public VerticalAlign componentVerticalAlignment = VerticalAlign.BASELINE;

    public DecorationData.ShowMandatory showMandatory = ShowMandatory.Mandatory;

    public boolean hideInfoHolder = false;

    // various construction:
    public DecorationData() {
    }

    // first set of construction:
    public DecorationData(int labelWidth, int componentWidth) {
        this.labelWidth = labelWidth;
        this.labelUnit = Unit.PX;
        this.componentWidth = componentWidth;
        this.componentUnit = Unit.PX;
    }

    public DecorationData(double labelWidth, double componentWidth) {
        this.labelWidth = labelWidth;
        this.labelUnit = Unit.EM;
        this.componentWidth = componentWidth;
        this.componentUnit = Unit.EM;
    }

    public DecorationData(double labelWidth, Unit labelUnit, double componentWidth, Unit componentUnit) {
        this.labelWidth = labelWidth;
        this.labelUnit = labelUnit;
        this.componentWidth = componentWidth;
        this.componentUnit = componentUnit;
    }

    // second one - with alignment:
    //
    public DecorationData(int labelWidth, HorizontalAlignmentConstant labelAlignment, int componentWidth) {
        this.labelWidth = labelWidth;
        this.labelUnit = Unit.PX;
        this.labelAlignment = labelAlignment;
        this.componentWidth = componentWidth;
    }

    public DecorationData(double labelWidth, HorizontalAlignmentConstant labelAlignment, double componentWidth) {
        this.labelWidth = labelWidth;
        this.labelUnit = Unit.EM;
        this.labelAlignment = labelAlignment;
        this.componentWidth = componentWidth;
    }

    public DecorationData(double labelWidth, Unit labelUnit, HorizontalAlignmentConstant labelAlignment, double componentWidth, Unit componentUnit,
            HorizontalAlignmentConstant componentAlignment) {
        this.labelWidth = labelWidth;
        this.labelUnit = labelUnit;
        this.labelAlignment = labelAlignment;
        this.componentWidth = componentWidth;
        this.componentUnit = componentUnit;
        this.componentAlignment = componentAlignment;
    }

}