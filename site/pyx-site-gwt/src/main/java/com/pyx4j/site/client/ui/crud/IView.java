/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.ui.crud.misc.IMemento;

public interface IView extends IsWidget {

    public interface Presenter {

        void populate();

        @Deprecated
        //TODO see if it is really needed for editor?
        void refresh();
    }

    IMemento getMemento();

    void storeState(Place place);

    void restoreState();

    void showVisor(IsWidget widget, String caption);

    void hideVisor();

    boolean isVisorShown();

}
