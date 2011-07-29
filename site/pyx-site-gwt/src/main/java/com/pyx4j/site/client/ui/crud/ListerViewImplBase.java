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
 * Created on 2011-05-18
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;

public class ListerViewImplBase<E extends IEntity> extends DockLayoutPanel implements IListerView<E> {

    protected static I18n i18n = I18nFactory.getI18n(ListerViewImplBase.class);

    protected ListerBase<E> lister = null;

    public ListerViewImplBase() {
        super(Unit.EM);
    }

    public ListerViewImplBase(ListerBase<E> lister) {
        super(Unit.EM);
        setLister(lister);
    }

    public ListerViewImplBase(Widget header, double size) {
        super(Unit.EM);
        addNorth(header, size);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setLister(ListerBase<E> lister) {
        if (getCenter() == null) { // finalise UI here:
            add(new ScrollPanel());
            setSize("100%", "100%");
        }

        if (this.lister == lister) {
            return; // already!?.
        }

        ((ScrollPanel) getCenter()).setWidget(this.lister = lister);
    }

    @Override
    public ListerBase<E> getLister() {
        return lister;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        assert (lister != null);
        lister.setPresenter(presenter);
    }

    @Override
    public int getPageSize() {
        assert (lister != null);
        return lister.getPageSize();
    }

    @Override
    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData) {
        assert (lister != null);
        lister.populateData(entityes, pageNumber, hasMoreData);
    }
}
