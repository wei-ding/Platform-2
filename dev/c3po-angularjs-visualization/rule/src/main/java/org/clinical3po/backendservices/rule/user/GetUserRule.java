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
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;

/**
 * Created by w.ding on 8/28/2015.
 *
 * Get a single user profile
 *
 * AccessLevel R [user]
 *
 */
public class GetUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String email = (String) data.get("email");
        String userId = (String) data.get("userId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            if(rid != null) {
                OrientVertex user = (OrientVertex)DbService.getVertexByRid(graph, rid);
                if(user != null) {
                    inputMap.put("result", user.getRecord().toJSON());
                    return true;
                } else {
                    inputMap.put("result", "User with rid " + rid + " cannot be found.");
                    inputMap.put("responseCode", 404);
                    return false;
                }
            } else if(userId != null) {
                OrientVertex user = (OrientVertex)getUserByUserId(graph, userId);
                if(user != null) {
                    inputMap.put("result", user.getRecord().toJSON());
                    return true;
                } else {
                    inputMap.put("result", "User with userId " + userId + " cannot be found.");
                    inputMap.put("responseCode", 404);
                    return false;
                }
            } else if(email != null) {
                OrientVertex user = (OrientVertex)getUserByEmail(graph, email);
                if(user != null) {
                    inputMap.put("result", user.getRecord().toJSON());
                    return true;
                } else {
                    inputMap.put("result", "User with email " + email + " cannot be found.");
                    inputMap.put("responseCode", 404);
                    return false;
                }
            } else {
                inputMap.put("result", "@rid or userId or email is required.");
                inputMap.put("responseCode", 400);
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
    }
}
