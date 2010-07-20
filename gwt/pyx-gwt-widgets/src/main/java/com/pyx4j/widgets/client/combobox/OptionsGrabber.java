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
 * Created on Jul 20, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.combobox;

import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class OptionsGrabber<E> {

    public interface Callback<E> {
        void onOptionsReady(Request request, Response<E> response);
    }

    public static class Request implements IsSerializable {

        //-1 unlimited
        private int limit = -1;

        private String query;

        private int pageNumber;

        public Request() {
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

    }

    public static class Response<E> implements IsSerializable {
        private Collection<E> options;

        public Response() {
        }

        public Response(Collection<E> options) {
            setOptions(options);
        }

        public Collection<E> getOptions() {
            return this.options;
        }

        public void setOptions(Collection<E> options) {
            this.options = options;
        }
    }

    public abstract void obtainOptions(Request request, Callback<E> callback);

}
