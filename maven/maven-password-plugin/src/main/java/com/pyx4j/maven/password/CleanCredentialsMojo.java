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
 * Created on 2011-02-02
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.password;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Remove created credentials file.
 * 
 */
@Mojo(name = "clean", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, threadSafe = true)
public class CleanCredentialsMojo extends CredentialsFileAbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        File file = new File(locationDir, credentialsName);
        if (file.exists()) {
            file.delete();
        }
    }

}
