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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * 
 * Read the credentials from server in settings.xml and set them as project properties.
 * 
 * @goal set-project-properties
 */
public class SetCredentialsPropertiesMojo extends CredentialsAbstractMojo implements Contextualizable {

    /**
     * The server id in maven settings.xml to use for email(username) and password.
     * 
     * @parameter
     * @required
     */
    protected String serverId;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Server srv = settings.getServer(serverId);
        if (srv == null) {
            throw new MojoExecutionException("ServerId " + serverId + " not found");
        }

        String password = decryptPassword(srv.getPassword());

        project.getProperties().put(usernameName, srv.getUsername());
        project.getProperties().put(passwordName, password);
    }

    @Override
    public void contextualize(Context context) throws ContextException {
        this.container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

}
