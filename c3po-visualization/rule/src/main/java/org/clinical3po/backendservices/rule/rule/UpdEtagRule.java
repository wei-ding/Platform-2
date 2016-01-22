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

package org.clinical3po.backendservices.rule.rule;

import org.clinical3po.backendservices.rule.Rule;

import java.util.Map;

/**
 * Created by w.ding on 14/03/15.
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 *
 */
public class UpdEtagRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        String error = updateValidation(inputMap, eventData);
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            eventData.put("enableEtag", data.get("enableEtag"));
            String cacheControl = (String)data.get("cacheControl");
            if(cacheControl != null) {
                eventData.put("cacheControl", cacheControl);
            }
            eventData.put("updateDate", new java.util.Date());
            return true;
        }
    }
}
