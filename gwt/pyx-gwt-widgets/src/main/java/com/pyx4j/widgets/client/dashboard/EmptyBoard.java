/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 9, 2014
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class EmptyBoard extends SimplePanel implements IBoard {
    private SimplePanel panel;

    private BoardLayout layoutType;

    public EmptyBoard(String message) {
        setMessage(message);
    }

    @Override
    public Widget asWidget() {
        if (panel == null) {
            panel = new SimplePanel();
            panel.getElement().getStyle().setProperty("padding", "0 10px");
        }
        return panel;
    }

    public void setMessage(String message) {
        asWidget();
        panel.setWidget(new HTML(message));
    }

    @Override
    public BoardLayout getLayout() {
        return layoutType;
    }

    @Override
    public boolean setLayout(BoardLayout layoutType) {
        this.layoutType = layoutType;
        return true;
    }

    @Override
    public void addGadget(IGadget gadget) {
    }

    @Override
    public void addGadget(IGadget gadget, int column) {
    }

    @Override
    public void insertGadget(IGadget gadget, int column, int row) {
    }

    @Override
    public void addEventHandler(BoardEvent handler) {
    }

    @Override
    public void setReadOnly(boolean isReadOnly) {
    }

    @Override
    public IGadgetIterator getGadgetIterator() {
        return new IGadgetIterator() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public IGadget next() {
                return null;
            }

            @Override
            public void remove() {
            }

            @Override
            public int getColumn() {
                return 0;
            }
        };
    }
}
