package quangnt;

import java.util.HashMap;
import java.util.Map;

public class NodeDecisionTree {
    String condition;
    String result;
    String type;
    Map<String, NodeDecisionTree> childs;

    public NodeDecisionTree() {
        childs = new HashMap<String, NodeDecisionTree>();
    }

    public NodeDecisionTree(String condition) {
        this.condition = condition;
        childs = new HashMap<String, NodeDecisionTree>();
    }

    public void addChildNode(NodeDecisionTree node, String condition) {
        childs.put(condition, node);
    }
}
