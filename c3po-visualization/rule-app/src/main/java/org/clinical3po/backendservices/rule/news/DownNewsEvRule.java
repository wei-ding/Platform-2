package org.clinical3po.backendservices.rule.news;

import org.clinical3po.backendservices.rule.AbstractBfnRule;
import org.clinical3po.backendservices.rule.Rule;

/**
 * Created by hus5 on 3/6/2015.
 */
public class DownNewsEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return downBranchEv("forum", objects);
    }
}
