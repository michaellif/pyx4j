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

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

public class ListerInternalViewImplBase<E extends IEntity> extends ScrollPanel implements IListerView<E> {

    protected static I18n i18n = I18nFactory.getI18n(ListerInternalViewImplBase.class);

    protected ListerBase<E> lister = null;

    public ListerInternalViewImplBase() {
    }

    public ListerInternalViewImplBase(ListerBase<E> lister) {
        this();
        setLister(lister);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setLister(ListerBase<E> lister) {
        if (this.lister == lister) {
            return; // already!?.
        }

        setWidget(this.lister = lister);
    }

    @Override
    public ListerBase<E> getLister() {
        assert (lister != null);
        return lister;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        getLister().setPresenter(presenter);
    }

    @Override
    public Presenter getPresenter() {
        return getLister().getPresenter();
    }

    @Override
    public int getPageSize() {
        return getLister().getPageSize();
    }

    @Override
    public int getPageNumber() {
        return getLister().getPageNumber();
    }

    @Override
    public void populate(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        getLister().populate(entityes, pageNumber, hasMoreData, totalRows);
    }

    @Override
    public List<FilterData> getFiltering() {
        return getLister().getFiltering();
    }

    @Override
    public List<Sort> getSorting() {
        return getLister().getSorting();
    }

    @Override
    public void setSorting(List<Sort> sorts) {
        getLister().setSorting(sorts);
    }

    @Override
    public IMemento getMemento() {
        return getLister().getMemento();
    }
}
