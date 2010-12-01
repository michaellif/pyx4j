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
 * Created on Dec 1, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.ria.client;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.ria.client.view.ILayoutManager;
import com.pyx4j.ria.client.view.IPosition;
import com.pyx4j.ria.client.view.IViewManager;

public abstract class AbstractViewManager<T extends IPosition> implements IViewManager<T> {

    private final ILayoutManager<T> layoutManager;

    public AbstractViewManager(ILayoutManager<T> layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void addView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).addView(view);
    }

    @Override
    public void showView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).showView(view);
    }

    @Override
    public void addAndShowView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).addView(view);
        layoutManager.getFolder(position).showView(view);
    }

    @Override
    public void closeView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).removeView(view);
    }
}
