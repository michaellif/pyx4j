/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation.datagrid;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Element;

/**
 * This hack is required to fix the invisible vertical scroll bar in the DataGrid.
 * According to my investigation it's been caused by broken CSS defined in 'NativeHorizontalScrollbar.ui.xml' namely, this CSS defines
 * style for '.scrollable' with 'left' property which seems to ruin everything: since the element that uses this style needs to be positioned
 * by the right corner and overflow to the left, so that the scroll bar becomes visible. However it seems that 'left' conflicts with 'width'. Anyhow it was
 * experienced with GWT 2.5.1, and this file seems to fix it.
 * 
 * This fix expects that GWT build doesn't minify CSS 'style names'.
 * 
 * @author ArtyomB
 */
public class DataGridScrollFixerHack {

    public static void apply(DataGrid<?> dataGrid) {
        apply(dataGrid.getElement());
    }

    //@formatter:off
    private native static void apply(Element element) /*-{
		var scrollable = element
				.getElementsByClassName('scrollable nativeVerticalScrollbar')[0];
		scrollable.className = new String(scrollable.className).replace(
				'scrollable', '');

		scrollable.style.position = 'absolute';
		scrollable.style.right = '0px';
		scrollable.style.width = '100px';
		scrollable.style.top = '0px';
		scrollable.style.height = '100%';

		scrollable.style.overflowX = 'hidden';
		scrollable.style.overflowY = 'scroll';

		console.log("modified: " + scrollable);
    }-*/;
    //@formatter:on

}
