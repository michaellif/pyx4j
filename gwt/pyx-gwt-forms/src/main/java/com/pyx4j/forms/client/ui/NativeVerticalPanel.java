/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NativeVerticalPanel extends VerticalPanel implements INativeSimplePanel {

    private final CPanelBase panel;

    public NativeVerticalPanel(CPanelBase panel) {
        super();
        this.panel = panel;
        setWidth(panel.getWidth());
        setHeight(panel.getHeight());
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void add(INativeComponent nativeWidget, CLayoutConstraints layoutConstraints) {
        super.add((Widget) nativeWidget);
        setCellWidth((Widget) nativeWidget, "100%");

        GWTStyleAdapter.setLayoutConstraints((Widget) nativeWidget, layoutConstraints);
        //        if (layoutConstraints != null && layoutConstraints.anchor != null) {
        //            switch (layoutConstraints.anchor) {
        //            case LOWER_RIGHT:
        //                Widget td = ((Widget) nativeWidget).getParent();
        //                setCellHorizontalAlignment(td.getElement(), VerticalPanel.ALIGN_RIGHT);
        //                setCellVerticalAlignment(td.getElement(), VerticalPanel.ALIGN_BOTTOM);
        //                break;
        //            }
        //
        //        }
    }

    @Override
    public CPanelBase getCComponent() {
        return panel;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub

    }
}
