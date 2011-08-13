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
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.rpc.CrudAppPlace;

public interface IListerView<E extends IEntity> extends IView<E> {

    public interface Presenter {

        void setParentFiltering(Key parentID);

        public List<FilterData> getPreDefinedFilters();

        void setPreDefinedFilters(List<FilterData> filters);

        void populate(final int pageNumber);

        void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID);

        void edit(Class<? extends CrudAppPlace> openPlaceClass, Key itemID);

        void editNew(Class<? extends CrudAppPlace> openPlaceClass, Key parentID);
    }

    void setPresenter(Presenter presenter);

    Presenter getPresenter();

    ListerBase<E> getLister();

    int getPageSize();

    void populate(List<E> entityes, int pageNumber, boolean hasMoreData);

    List<FilterData> getFiltering();

    List<Sort> getSorting();
}
