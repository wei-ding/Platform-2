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

import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 11/12/14.
 *
 * Execute database command so that it can create schemas or other objects.
 *
 * Due to the importance of the API, the code level access control is in place.
 *
 * AccessLevel R [owner, admin, dbAdmin]
 *
 * Current AccessLevel R [owner]
 * as site level control is not done yet. TODO
 */
public class ExecUpdateCmdRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        // make sure we have content payload here.
        String script = (String)data.get("script");
        if(script == null || script.length() == 0) {
            error = "Content is empty";
            inputMap.put("responseCode", 400);
        } else {
            // make sure the script is executable.
            String result = execUpdateCmd(data, false);
            if(result.length() > 0) {
                error = result;
                inputMap.put("responseCode", 500);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("script", script);
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
