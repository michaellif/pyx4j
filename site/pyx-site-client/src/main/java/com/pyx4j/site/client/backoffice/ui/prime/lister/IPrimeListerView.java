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
 * Created on 2011-05-03
 * @author Vlad
 */
package com.pyx4j.site.client.backoffice.ui.prime.lister;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView.IPrimeListerPresenter;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

public interface IPrimeListerView<E extends IEntity> extends IPrimePaneView<IPrimeListerPresenter<E>> {

    public interface IPrimeListerPresenter<E extends IEntity> extends IPrimePanePresenter {

        void setParentKey(Key parentID);

        void setParentKey(Key parentID, Class<? extends IEntity> parentClass);

        void setPreDefinedFilters(List<Criterion> filters);

        void addPreDefinedFilters(List<Criterion> filters);

        void addPreDefinedFilter(Criterion filter);

        void clearPreDefinedFilters();

    }

    SiteDataTablePanel<E> getDataTablePanel();

    int getPageSize();

    int getPageNumber();

    void discard();

    List<Criterion> getFilters();

    void setFilters(List<Criterion> filterData);

    List<Sort> getSortCriteria();

    void setSortCriteria(List<Sort> sorts);

    void onDeleted(Key itemID, boolean isSuccessful);

}
