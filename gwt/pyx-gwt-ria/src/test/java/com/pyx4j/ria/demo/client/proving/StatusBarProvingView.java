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
 * Created on May 17, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.pyx4j.ria.client.ImageFactory;

public class StatusBarProvingView extends AbstractProvingView {

    public StatusBarProvingView() {
        super("StatusBar Range", ImageFactory.getImages().debugOn());

        ActionGroup progress = createActionGroup("ProgressBar");

        progress.addAction("start", new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });

        progress.addAction("stop", new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });

        ActionGroup message = createActionGroup("StatusBar Message");

        message.addAction("info(s)", new Runnable() {
            @Override
            public void run() {

                //TODO  Logger.info("Some info");
            }
        });

        message.addAction("warn(s)", new Runnable() {
            @Override
            public void run() {

                //TODO Logger.warn("Some warn");
            }
        });

        message.addAction("error(s)", new Runnable() {
            @Override
            public void run() {

                // TODO Logger.error("Some error");
            }
        });

        ActionGroup icons = createActionGroup("StatusBar icons");

        icons.addAction("activate", new Runnable() {
            @Override
            public void run() {

                // TODO Logger.error("TODO migrate");
            }
        });
    }

}
