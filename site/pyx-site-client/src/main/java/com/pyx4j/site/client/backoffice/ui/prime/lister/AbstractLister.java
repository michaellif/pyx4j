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
package com.pyx4j.site.client.backoffice.ui.prime.lister;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.AbstractPrimePane;

public class AbstractLister<E extends IEntity> extends AbstractPrimePane implements ILister<E> {

    protected EntityDataTablePanel<E> lister = null;

    public AbstractLister() {
        super();

    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setLister(EntityDataTablePanel<E> lister) {
        if (getContentPane() == null) { // finalise UI here:
            setContentPane(new ScrollPanel());
            setSize("100%", "100%");
        }

        if (this.lister == lister) {
            return; // already!?.
        }

        lister.getElement().getStyle().setPaddingBottom(40, Unit.PX);

        ((ScrollPanel) getContentPane()).add(this.lister = lister);
    }

    @Override
    public EntityDataTablePanel<E> getLister() {
        assert (lister != null);
        return lister;
    }

    @Override
    public void setPresenter(ILister.Presenter<E> presenter) {
        getLister().setPresenter(presenter);
        setCaption(presenter != null && presenter.getPlace() != null ? AppSite.getHistoryMapper().getPlaceInfo(presenter.getPlace()).getCaption() : "");
    }

    @Override
    public ILister.Presenter<E> getPresenter() {
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
    public List<Criterion> getFilters() {
        return getLister().getFilters();
    }

    @Override
    public void setFilters(List<Criterion> filterData) {
        getLister().setFilters(filterData);
    }

    @Override
    public List<Sort> getSortCriteria() {
        return getLister().getSortCriteria();
    }

    @Override
    public void setSortCriteria(List<Sort> sorts) {
        getLister().setSortCriteria(sorts);
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        // TODO Auto-generated method stub
    }

    @Override
    public void discard() {
        getLister().discard();
    }

}
