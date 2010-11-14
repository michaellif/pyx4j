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
 * Created on Nov 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.ria.client.view.FourFoldersLayout;
import com.pyx4j.ria.client.view.FourFoldersPosition;
import com.pyx4j.ria.client.view.Position;
import com.pyx4j.ria.client.view.ViewManager;

public class MainPanel extends FourFoldersLayout implements ViewManager<FourFoldersPosition> {

    @Override
    public Position getPositionForView(Class<? extends AbstractView> viewClass) {
        if (TestView.class.equals(viewClass)) {
            return FourFoldersPosition.center;
        } else {
            return FourFoldersPosition.south;
        }
    }

    @Override
    public void addAndShowView(AbstractView view, boolean closable) {
        Position position = getPositionForView(view.getClass());
        getFolder(position).addView(view, closable);
        getFolder(position).showView(view);
    }

    @Override
    public void closeView(AbstractView view) {
        Position position = getPositionForView(view.getClass());
        getFolder(position).removeView(view, true);
    }

    @Override
    public void addView(AbstractView view, boolean closable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showView(AbstractView view) {
        // TODO Auto-generated method stub

    }

}
