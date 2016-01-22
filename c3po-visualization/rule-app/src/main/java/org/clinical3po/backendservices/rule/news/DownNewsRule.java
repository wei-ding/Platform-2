package org.clinical3po.backendservices.rule.news;

import org.clinical3po.backendservices.rule.AbstractBfnRule;
import org.clinical3po.backendservices.rule.Rule;

/**
 * Created by hus5 on 3/6/2015.
 *
 * Vote down news category
 *
 * AccessLevel R [user]
 *
 */
public class DownNewsRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return downBranch("news", objects);
    }
}
