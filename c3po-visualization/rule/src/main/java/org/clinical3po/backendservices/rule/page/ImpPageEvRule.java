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

import com.hazelcast.core.ITopic;
import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.Map;

/**
 * Created by w.ding on 10/24/2015.
 */
public class ImpPageEvRule extends AbstractPageRule implements Rule {
    public boolean execute(Object... objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        //ITopic topic = ServiceLocator.getInstance().getHzInstance().getTopic("page");
        //topic.publish(eventMap);
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        impPage(data);
        return true;
    }
}
