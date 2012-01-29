/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 29, 2012
 * @author David
 * @version $Id$
 */
package com.pyx4j.entity.report.master;

import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import com.pyx4j.entity.report.JasperReportModel;

public class MasterDataSource implements JRRewindableDataSource {

    private final List<MasterReportEntry> list;

    private Iterator<MasterReportEntry> iterator;

    private MasterReportEntry current;

    public MasterDataSource(List<MasterReportEntry> list) {
        this.list = list;
    }

    @Override
    public boolean next() throws JRException {
        if (iterator == null)
            iterator = list.iterator();

        if (iterator.hasNext()) {
            current = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if (jrField.getValueClass() != JasperReportModel.class) {
            return null;
        }

        if (jrField.getName().equals("fullReport"))
            return current.getFullReport();
        else if (jrField.getName().equals("leftReport"))
            return current.getLeftReport();
        else if (jrField.getName().equals("rightReport"))
            return current.getRightReport();
        else
            return null;
    }

    @Override
    public void moveFirst() throws JRException {
        iterator = null;
    }
}
