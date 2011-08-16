/*
 * Copyright 2011 Andreas Sahlbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sahlbach.maven.delivery;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * User: Andreas Sahlbach
 * Date: 14.08.11
 * Time: 11:19
 */
@SuppressWarnings ( {"unchecked"})
@Test
public class DeliveryMojoTest {

    private DeliveryMojo mojo;
    private RepositorySystem repoSystem;
    private RepositorySystemSession repoSession;
    private List repositoryList;

    @BeforeTest
    public void setup() {
        mojo = new DeliveryMojo();

        repoSystem = mock(RepositorySystem.class);
        repoSession = mock(RepositorySystemSession.class);
        repositoryList = mock(List.class);

        mojo.setRemoteRepos(repositoryList);
        mojo.setRepoSession(repoSession);
        mojo.setRepoSystem(repoSystem);
    }

    @Test
    public void testSkip() throws Exception {
        mojo.setSkip(true);
        mojo.execute();
        verifyZeroInteractions(repoSystem,repoSession,repositoryList);
    }
}
