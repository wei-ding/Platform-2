package org.clinical3po.backendservices.rule.catalog;

import org.clinical3po.backendservices.rule.Rule;
import org.clinical3po.backendservices.util.HashUtil;
import org.clinical3po.backendservices.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;

/**
 * Created by w.ding on 30/03/15.
 *
 * AccessLevel R [owner, admin, catalogAdmin]
 */
public class AddProductRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addProduct(objects);
    }
}
