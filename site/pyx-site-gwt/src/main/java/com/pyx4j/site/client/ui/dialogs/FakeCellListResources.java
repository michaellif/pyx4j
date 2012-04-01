/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 19, 2012
 * @author artem
 * @version $Id$
 */
package com.pyx4j.site.client.ui.dialogs;

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
