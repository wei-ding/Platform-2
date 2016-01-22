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

import org.clinical3po.backendservices.rule.AbstractRule;
import org.clinical3po.backendservices.rule.Rule;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 16/02/15.
 *
 * Get all request transform rules for a ruleClass
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 *
 */
public class GetTransformRequestRule extends AbstractTransformRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(GetTransformRequestRule.class);
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String ruleClass = (String)data.get("ruleClass");
        Map ruleMap = AbstractRule.getRuleByRuleClass(ruleClass);
        if(ruleMap != null) {
            List<Map<String, Object>> reqTransforms = (List) ruleMap.get("reqTransforms");
            if(reqTransforms != null && reqTransforms.size() > 0) {
                inputMap.put("result", mapper.writeValueAsString(reqTransforms));
                return true;
            } else {
                inputMap.put("result", "No transform can be found for ruleClass" + ruleClass);
                inputMap.put("responseCode", 404);
                return false;
            }
        } else {
            inputMap.put("result", "No rule can be found for ruleClass" + ruleClass);
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
