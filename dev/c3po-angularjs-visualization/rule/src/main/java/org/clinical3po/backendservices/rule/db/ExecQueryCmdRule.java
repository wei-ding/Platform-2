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

package org.clinical3po.backendservices.rule.db;

import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.util.ServiceLocator;
import org.clinical3po.backendservices.util.Util;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 01/03/15.
 */
public class ExecQueryCmdRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String script = (String)data.get("script");
        // make sure the script is executable.
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        String json = null;
        try{
            json = execQueryCmd(graph, data);
        } catch (Exception e) {
            logger.error("Exception:", e);
            inputMap.put("result", e.getMessage());
            inputMap.put("responseCode", 500);
            return false;
        } finally {
            graph.shutdown();
        }
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("responseCode", 404);
            inputMap.put("result", "Not found");
            return false;
        }
    }
}
