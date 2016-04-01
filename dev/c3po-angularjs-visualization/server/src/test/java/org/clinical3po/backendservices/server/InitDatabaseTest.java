/*
 * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.
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

package org.clinical3po.backendservices.server;

import org.clinical3po.backendservices.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.xml.ws.Service;
import java.util.List;

/**
 * Created by w.ding on 9/4/2015.
 */
public class InitDatabaseTest  extends TestCase {

    public InitDatabaseTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(InitDatabaseTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMenu() {
        try {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select from Menu where host = ?");
            List<ODocument> result = graph.getRawGraph().command(query).execute("example");
            if(result.size() > 0) {
                ODocument doc = result.get(0);
                System.out.println("doc=" + doc.toJSON());
                System.out.println("doc=" + doc.toJSON("fetchPlan:*:2"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
