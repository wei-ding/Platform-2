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

package org.clinical3po.backendservices.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import org.clinical3po.backendservices.rule.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 9/4/2015.
 *
 * Overwrite if the form exists in db.
 *
 * AccessLevel R [user, admin, formAdmin]
 *
 */
public class ImpFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;

        String host = (String)user.get("host");
        Map<String, Object> dataMap = mapper.readValue((String)data.get("content"), new TypeReference<HashMap<String, Object>>() {});
        String formId = (String)dataMap.get("formId");
        if(host != null) {
            if(!host.equals(data.get("host"))) {
                error = "User can only import form from host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                if(!formId.contains(host)) {
                    // you are not allowed to add form as it is not owned by the host.
                    error = "form id doesn't contain host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    // Won't check if form exists or not here.
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);

                    eventData.put("formId", formId);
                    eventData.put("action", dataMap.get("action"));
                    eventData.put("schema", dataMap.get("schema"));
                    eventData.put("form", dataMap.get("form"));
                    if(dataMap.get("modelData") != null) eventData.put("modelData", dataMap.get("modelData"));

                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
                }
            }
        } else {
            // This is owner to import form, notice no host is passed in.
            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);

            eventData.put("formId", formId);
            eventData.put("action", dataMap.get("action"));
            eventData.put("schema", dataMap.get("schema"));
            eventData.put("form", dataMap.get("form"));
            if(dataMap.get("modelData") != null) eventData.put("modelData", dataMap.get("modelData"));

            eventData.put("createDate", new java.util.Date());
            eventData.put("createUserId", user.get("userId"));
        }

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
