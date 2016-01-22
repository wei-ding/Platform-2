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

import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.rule.page.AbstractPageRule;

import java.util.Map;

/**
 * Created by w.ding on 14/02/15.
 *
 * This is the rule that called by form loader.
 * It get all the forms and return a map from id to the content.
 *
 * accessLevel is owner by default.
 */
public class GetFormMapRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)user.get("host");

        String forms = getFormMap(host);
        if(forms != null) {
            inputMap.put("result", forms);
            return true;
        } else {
            inputMap.put("result", "No page can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
