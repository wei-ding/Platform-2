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
import org.clinical3po.backendservices.server.DbService;

import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 21/12/14.
 * Download events from event store. Should be a search interface so that you can enter search
 * criteria.
 *
 * TODO make it searchable
 *
 * AccessLevel R [user]
 *
 */
public class DownloadEventRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        // everyone is allowed to download events performed by himself and replay it on
        // other site build with the same framework.

        // make sure that both from datetime and to datetime are in the past. And to datetime
        // can be optional which means get everything after from datetime.

        // Now let's build a criteria for db search.

        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner")) {
            // only owner can generate events for common components without host.
            data.remove("host");
        }
        data.put("createUserId", user.get("userId"));
        String json = DbService.getData("Event", data);
        if(json != null) {
            inputMap.put("result", json);
        } else {
            error = "No event can be found";
            inputMap.put("responseCode", 400);
        }

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
