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

package org.clinical3po.backendservices.rule.host;

import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.util.ServiceLocator;

import java.util.*;

/**
 * Created by w.ding on 2015-01-19.
 *
 * AccessLevel R [user]
 */
public class GetHostDropdownRule extends AbstractHostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Set<String> keys = ServiceLocator.getInstance().getHostMap().keySet();
        for(String key: keys) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("label", key);
            map.put("value", key);
            list.add(map);
        }
        String hostDropdown = mapper.writeValueAsString(list);
        if(hostDropdown != null) {
            inputMap.put("result", hostDropdown);
            return true;
        } else {
            inputMap.put("result", "No host can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
