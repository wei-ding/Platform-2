package org.clinical3po.backendservices.rule.blog;

import org.clinical3po.backendservices.rule.AbstractBfnRule;
import org.clinical3po.backendservices.rule.Rule;

/**
 * Created by w.ding on 3/6/2015.
 * Update post in a blog
 *
 * AccessLevel R [owner, admin, blogAdmin, blogUser]
 *
 * blogUser can only update his or her blog
 *
 */
public class UpdPostRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPost("blog", objects);
    }
}
