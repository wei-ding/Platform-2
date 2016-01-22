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

package org.clinical3po.backendservices.rule.transform;

import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.rule.role.AbstractRoleRule;

import java.util.Map;

/**
 * Created by w.ding on 16/02/15.
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 *
 */
public class AddTransformRequestRule extends AbstractTransformRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;
        String host = (String)user.get("host");
        String ruleClass = (String)data.get("ruleClass");
        Integer sequence = (Integer)data.get("sequence");
        if(host != null) {
            // admin or ruleAdmin adding transform rule for their site.
            if(!host.equals(data.get("host"))) {
                error = "User can only add transform rule from host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                // check if the ruleClass belongs to the host.
                if(!ruleClass.contains(host)) {
                    // you are not allowed to update transform rule to the rule as it is not owned by the host.
                    error = "ruleClass is not owned by the host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    String json = getTransformRequestBySeq(ruleClass, sequence);
                    if(json != null) {
                        error = "Transform rule exists for the sequence";
                        inputMap.put("responseCode", 400);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("ruleClass", data.get("ruleClass"));
                        eventData.put("sequence", data.get("sequence"));
                        eventData.put("transformRule", data.get("transformRule"));
                        eventData.put("transformData", data.get("transformData"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                    }
                }
            }
        } else {
            String json = getTransformRequestBySeq(ruleClass, sequence);
            if(json != null) {
                error = "Transform rule exists for the sequence";
                inputMap.put("responseCode", 400);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("ruleClass", data.get("ruleClass"));
                eventData.put("sequence", data.get("sequence"));
                eventData.put("transformRule", data.get("transformRule"));
                eventData.put("transformData", data.get("transformData"));
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
            }
        }

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
