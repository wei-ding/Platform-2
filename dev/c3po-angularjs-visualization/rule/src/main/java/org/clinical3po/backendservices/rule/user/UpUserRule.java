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
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;

/**
 * Created by w.ding on 10/17/2015.
 *
 * Vote up a user
 *
 * AccessLevel R [user]
 */
public class UpUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;

        Map<String,Object> userMap = (Map<String, Object>)payload.get("user");
        String voteUserId = (String)userMap.get("userId");
        String userRid = (String)data.get("@rid");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex user = (OrientVertex)DbService.getVertexByRid(graph, userRid);
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", voteUserId);
            if(user == null || voteUser == null) {
                error = "User or vote user cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                // check if this VoteUserId has down voted user before.
                boolean voted = false;
                for (Edge edge : voteUser.getEdges(user, Direction.OUT, "UpVote")) {
                    if(edge.getVertex(Direction.IN).equals(user)) voted = true;
                }
                if(voted) {
                    error = "You have up vote the user already";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("userId", user.getProperty("userId"));
                    eventData.put("voteUserId", voteUserId);
                    eventData.put("updateDate", new java.util.Date());
                }
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
