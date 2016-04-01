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
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.io.IOException;

/**
 * Created by w.ding on 10/8/2015.
 */
public class ImportDatabase {

    public static void main(final String[] args) {
        imp();
    }

    public static void imp() {

        OrientGraph graph = ServiceLocator.getInstance().getGraph();

        try{
            OCommandOutputListener listener = new OCommandOutputListener() {
                @Override
                public void onMessage(String iText) {
                    System.out.print(iText);
                }
            };

            ODatabaseImport imp = new ODatabaseImport(graph.getRawGraph(), "/temp/export/export.json.gz", listener);
            imp.importDatabase();
            imp.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            graph.shutdown();
        }

    }

}
