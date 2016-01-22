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

package org.clinical3po.backendservices.rule.user;

import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.server.DbService;
import org.clinical3po.backendservices.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.Map;

/**
 * Created by w.ding on 9/24/2015.
 *
 * log out a user.
 *
 * AccessLevel R [user]
 *
 */
public class LogOutUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        // The assumption is that user should have a token when he/she click logout.
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String)user.get("@rid");
        // check if the rid exists or not. if exists, then create event.
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex vertex = DbService.getVertexByRid(graph, rid);
            if(vertex != null) {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("userId", vertex.getProperty("userId"));
                eventData.put("host", data.get("host"));
                eventData.put("refreshToken", data.get("refreshToken"));
                eventData.put("logOutDate", new java.util.Date());
            } else {
                error = "User with @rid " + rid + " cannot be found.";
                inputMap.put("responseCode", 404);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
