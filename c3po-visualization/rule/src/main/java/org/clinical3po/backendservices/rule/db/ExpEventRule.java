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

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 12/12/14.
 *
 * Export only the event store for backup. There is no UI as of yet. TODO.
 *
 * Only owner can export the entire event store
 *
 * AccessLevel R [owner]
 *
 */
public class ExpEventRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("dbAdmin")) {
                error = "Role owner or admin or dbAdmin is required to add schema";
                inputMap.put("responseCode", 401);
            } else {
                String path = (String)data.get("path");
                if(path != null) {
                    // make sure that the Path exists.
                    File file = new File(path);
                    if (!file.exists()) {
                        String result = exportEvent(path);
                        inputMap.put("result", result);
                    } else {
                        error = "Please remove existing file manually";
                        inputMap.put("responseCode", 400);
                    }
                } else {
                    error = "Path is required";
                    inputMap.put("responseCode", 400);
                }
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
