/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 14, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.dialogs;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.CellList.Style;

/**
 * This is a hack that allows us to define style our own way (that's how I succeeded to override the selected item style, just writing own style for
 * cellListSelectedItem selector in VistaTheme didn't help).
 */
public class FakeCellListResources implements Resources {
    private final Style style = new CellList.Style() {
        @Override
        public boolean ensureInjected() {
            return true;
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String cellListEvenItem() {
            return "cellListEvenItem";
        }

        @Override
        public String cellListKeyboardSelectedItem() {
            return "cellListKeyboardSelectedItem";
        }

        @Override
        public String cellListOddItem() {
            return "cellListOddItem";
        }

        @Override
        public String cellListSelectedItem() {
            return "cellListSelectedItem";
        }

        @Override
        public String cellListWidget() {
            return "cellListWidget";
        }
    };

    @Override
    public ImageResource cellListSelectedBackground() {
        return null;
    }

    @Override
    public Style cellListStyle() {
        return style;
    }
}
