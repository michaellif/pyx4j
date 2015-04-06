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
 * Created on Feb 12, 2010
 * @author vlads
 */
package com.pyx4j.entity.server.dataimport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

public class DataPreloaderCollection extends AbstractDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(DataPreloaderCollection.class);

    protected final List<DataPreloader> childPreloaders = new Vector<DataPreloader>();

    public DataPreloaderCollection() {
        super();
    }

    public Vector<DataPreloaderInfo> getDataPreloaderInfo() {
        Vector<DataPreloaderInfo> dpis = new Vector<DataPreloaderInfo>();

        for (DataPreloader preloader : childPreloaders) {
            DataPreloaderInfo info = new DataPreloaderInfo();

            info.setDataPreloaderClassName(preloader.getClass().getName());

            HashMap<String, Serializable> params = new HashMap<String, Serializable>();
            for (String param : preloader.getParameters()) {
                params.put(param, null);
            }
            info.setParameters(params);
            dpis.add(info);
        }
        return dpis;
    }

    public void exectutePreloadersPrepare(Vector<DataPreloaderInfo> dpis) {
        if (dpis == null) {
            // Prepare all.
            for (DataPreloader preloader : childPreloaders) {
                preloader.prepare();
            }
        } else {
            for (DataPreloaderInfo info : dpis) {
                findPreloader: for (DataPreloader preloader : childPreloaders) {
                    if (preloader.getClass().getName().equals(info.getDataPreloaderClassName())) {
                        preloader.setParametersValues(info.getParameters());
                        preloader.prepare();
                        break findPreloader;
                    }
                }
            }
        }
    }

    public String exectutePreloadersCreate(Vector<DataPreloaderInfo> dpis) {
        StringBuilder b = new StringBuilder();
        for (DataPreloaderInfo info : dpis) {
            findPreloader: for (DataPreloader preloader : childPreloaders) {
                if (preloader.getClass().getName().equals(info.getDataPreloaderClassName())) {
                    preloader.setParametersValues(info.getParameters());
                    long start = System.currentTimeMillis();
                    String txt = preloader.create();
                    if (CommonsStringUtils.isStringSet(txt)) {
                        b.append(txt).append('\n');
                    }
                    String warn = "";
                    if (TimeUtils.since(start) > 4 * Consts.SEC2MSEC) {
                        warn = " ***";
                    }
                    b.append("\t\t").append(preloader.getClass().getSimpleName()).append(" ").append(TimeUtils.secSince(start)).append(warn).append("\n");
                    break findPreloader;
                }
            }
        }
        return b.toString();
    }

    public String exectutePreloadersDelete(Vector<DataPreloaderInfo> dpis) {
        StringBuilder b = new StringBuilder();
        for (DataPreloaderInfo info : dpis) {
            findPreloader: for (DataPreloader preloader : childPreloaders) {
                if (preloader.getClass().getName().equals(info.getDataPreloaderClassName())) {
                    preloader.setParametersValues(info.getParameters());
                    String txt = preloader.delete();
                    if (CommonsStringUtils.isStringSet(txt)) {
                        b.append(txt).append('\n');
                    }
                    break findPreloader;
                }
            }
        }
        return b.toString();
    }

    protected void add(DataPreloader preloader) {
        childPreloaders.add(preloader);
    }

    public String preloadAll() {
        StringBuilder b = new StringBuilder();
        String txt = delete();
        if (CommonsStringUtils.isStringSet(txt)) {
            b.append(txt).append('\n');
        }
        b.append(create());
        return b.toString();
    }

    @Override
    public String create() {
        StringBuilder b = new StringBuilder();
        for (final DataPreloader preloader : childPreloaders) {
            preloader.setParametersValues(parameters);
            log.debug("create preloader {}", preloader.getClass());
            long start = System.currentTimeMillis();

            String txt = new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess)
                    .execute(new Executable<String, RuntimeException>() {

                        @Override
                        public String execute() {
                            return preloader.create();
                        }

                    });

            if (CommonsStringUtils.isStringSet(txt)) {
                b.append(txt).append('\n');
            }

            String warn = "";
            if (TimeUtils.since(start) > 4 * Consts.SEC2MSEC) {
                warn = " ***";
            }
            b.append("\t\t").append(preloader.getClass().getSimpleName()).append(" ").append(TimeUtils.secSince(start)).append(warn).append("\n");
        }
        return b.toString();
    }

    @Override
    public String delete() {
        StringBuilder b = new StringBuilder();
        ListIterator<DataPreloader> rit = childPreloaders.listIterator(childPreloaders.size());
        while (rit.hasPrevious()) {
            DataPreloader preloader = rit.previous();
            preloader.setParametersValues(parameters);
            log.debug("delete preloader {}", preloader.getClass());
            String txt = preloader.delete();
            if (CommonsStringUtils.isStringSet(txt)) {
                b.append(txt).append('\n');
            }
        }
        return b.toString();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Class<? extends IEntity>> getEntityToDelete() {
        List<Class<? extends IEntity>> deleteList = new Vector();
        ListIterator<DataPreloader> rit = childPreloaders.listIterator(childPreloaders.size());
        while (rit.hasPrevious()) {
            DataPreloader preloader = rit.previous();
            preloader.setParametersValues(parameters);
            if (preloader instanceof AbstractDataPreloader) {
                deleteList.addAll(((AbstractDataPreloader) preloader).getEntityToDelete());
            } else {
                preloader.delete();
            }
        }
        return deleteList;
    }

}
