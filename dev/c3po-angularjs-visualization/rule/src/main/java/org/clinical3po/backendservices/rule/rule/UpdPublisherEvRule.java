package org.clinical3po.backendservices.rule.rule;

import org.clinical3po.backendservices.rule.Rule;

import java.util.Map;

/**
 * Created by hus5 on 3/10/2015.
 */
public class UpdPublisherEvRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updPublisher(data);
        return true;
    }
}
