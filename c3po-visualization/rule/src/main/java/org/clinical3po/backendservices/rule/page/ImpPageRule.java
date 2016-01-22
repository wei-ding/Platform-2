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

package org.clinical3po.backendservices.rule.page;

import org.clinical3po.backendservices.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 10/24/2015.
 *
 * AccessLevel R [owner, admin, pageAdmin]
 */
public class ImpPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;
        String host = (String)user.get("host");
        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        eventData.put("host", host);
        eventData.put("pageId", data.get("pageId"));
        eventData.put("content", data.get("content"));
        eventData.put("createDate", new java.util.Date());
        eventData.put("createUserId", user.get("userId"));
        if(host != null) {
            if (!host.equals(data.get("host"))) {
                error = "User can only import page from host: " + host;
                inputMap.put("responseCode", 403);
            }
        } else {
            eventData.remove("host");
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }

}
