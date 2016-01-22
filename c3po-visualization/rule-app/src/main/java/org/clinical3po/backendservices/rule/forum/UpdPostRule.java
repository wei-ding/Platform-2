package org.clinical3po.backendservices.rule.forum;

import org.clinical3po.backendservices.rule.AbstractBfnRule;
import org.clinical3po.backendservices.rule.Rule;

/**
 * Created by w.ding on 3/6/2015.
 * Update post in a forum
 *
 * AccessLevel R [owner, admin, forumAdmin, user]
 *
 * User can only update his or her post and there will be an indicate that
 * the post is updated. Maybe just the update date?
 *
 * for now to make it simple, user cannot update the post.
 *
 */
public class UpdPostRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPost("forum", objects);
    }

}
