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
import org.clinical3po.backendservices.rule.RuleEngine;
import org.clinical3po.backendservices.util.Util;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by w.ding on 8/25/2015.
 *
 * You don't need to check if the form is in db or not as the form should be cached
 * in memory image already while starting the server.
 *
 * AccessLevel A
 *
 */
public class GetFormRule extends AbstractFormRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(GetFormRule.class);

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String formId = (String)data.get("formId");
        String json = getFormById(inputMap);
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "Form with " + formId + " cannot be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
