package org.clinical3po.backendservices.rule.blog;

import org.clinical3po.backendservices.rule.AbstractBfnRule;
import org.clinical3po.backendservices.rule.Rule;

/**
 * Created by w.ding on 3/6/2015.
 */
public class UpdPostEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPostEv("blog", objects);
    }
}
