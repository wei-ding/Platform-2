package org.clinical3po.backendservices.rule.catalog;

import org.clinical3po.backendservices.rule.Rule;

import java.util.Map;

/**
 * Created by w.ding on 30/03/15.
 *
 * AccessLevel A
 */
public class GetCatalogTreeRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getBranchTree("catalog", objects);
    }
}
