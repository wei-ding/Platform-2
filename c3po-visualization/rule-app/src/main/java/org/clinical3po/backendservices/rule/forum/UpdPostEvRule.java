package org.clinical3po.backendservices.rule.forum;

import org.clinical3po.backendservices.rule.AbstractBfnRule;
import org.clinical3po.backendservices.rule.Rule;

/**
 * Created by w.ding on 21/03/15.
 */
public class UpdPostEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPostEv("forum", objects);
    }
}
