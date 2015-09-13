/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 12, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.server.servlet.ServletParametersUtils;
import com.pyx4j.essentials.server.servlet.ServletTextOutput;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.annotations.I18n;

@SuppressWarnings("serial")
public class TestTimeoutServlet extends HttpServlet {

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    @Transient
    public static interface Parameters extends IEntity {

        @Caption(description = "Continue writing to output for N seconds")
        IPrimitive<Integer> duration();

        @Caption(description = "Write to output every N seconds")
        IPrimitive<Integer> sleep();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletTextOutput out = new ServletTextOutput(response, false);

        Parameters parameters = EntityFactory.create(Parameters.class);
        parameters.duration().setValue(60 * 7);
        parameters.sleep().setValue(10);

        ServletParametersUtils.get(request, parameters);
        ServletParametersUtils.help(out, parameters);

        long start = System.currentTimeMillis();

        for (;;) {
            try {
                Thread.sleep(parameters.sleep().getValue(1) * Consts.SEC2MILLISECONDS);
            } catch (InterruptedException e) {
            }

            out.text("time: ", TimeUtils.secSince(start));

            if ((Math.abs(System.currentTimeMillis() - start) > parameters.duration().getValue(1) * Consts.SEC2MILLISECONDS)) {
                break;
            }
        }

        out.html("<p style=\"background-color:33FF33\">DONE</p>");

        IOUtils.closeQuietly(out);
    }

}
