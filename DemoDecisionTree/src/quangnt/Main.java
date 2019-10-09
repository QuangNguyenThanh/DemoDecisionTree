package quangnt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<DataRow> dataTraining = new ArrayList<>();

        dataTraining.add(new DataRow(1, "sunny", "hot", "high", "weak", "no"));
        dataTraining.add(new DataRow(2, "sunny", "hot", "high", "strong", "no"));
        dataTraining.add(new DataRow(3, "overcast", "hot", "high", "weak", "yes"));
        dataTraining.add(new DataRow(4, "rainy", "mild", "high", "weak", "yes"));
        dataTraining.add(new DataRow(5, "rainy", "cool", "normal", "weak", "yes"));
        dataTraining.add(new DataRow(6, "rainy", "cool", "normal", "strong", "no"));
        dataTraining.add(new DataRow(7, "overcast", "cool", "normal", "strong", "yes"));
        dataTraining.add(new DataRow(8, "sunny", "mild", "high", "weak", "no"));
        dataTraining.add(new DataRow(9, "sunny", "cool", "normal", "weak", "yes"));
        dataTraining.add(new DataRow(10, "rainy", "mild", "normal", "weak", "yes"));
        dataTraining.add(new DataRow(11, "sunny", "mild", "normal", "strong", "yes"));
        dataTraining.add(new DataRow(12, "overcast", "mild", "high", "strong", "yes"));
        dataTraining.add(new DataRow(13, "overcast", "hot", "normal", "weak", "yes"));
        dataTraining.add(new DataRow(14, "rainy", "mild", "high", "strong", "no"));
        dataTraining.add(new DataRow(15, "sunny", "cool", "high", "weak", "yes"));
        dataTraining.add(new DataRow(16, "overcast", "cool", "high", "weak", "no"));
        dataTraining.add(new DataRow(17, "overcast", "cool", "high", "strong", "yes"));

        DecisionTree tree = null;
        tree = splitTree(tree, dataTraining);

        printTree(tree);
        DataRow dataTest = new DataRow(20, "rainy", "mild", "high", "strong", null);

        test(tree, dataTest);
    }

    private static void test(DecisionTree tree, DataRow dataTest) {
        NodeDecisionTree currentNode = tree.root;
        findResult(currentNode, dataTest);
    }

    private static void findResult(NodeDecisionTree currentNode, DataRow dataTest) {
        if (currentNode.result != null) {
            System.out.println("Result: " + currentNode.result);
            return;
        }
        if (currentNode.condition == "outlook") {
            currentNode = currentNode.childs.get(dataTest.outlook);
        } else if (currentNode.condition == "temperature") {
            currentNode = currentNode.childs.get(dataTest.temperature);
        } else if (currentNode.condition == "humidity") {
            currentNode = currentNode.childs.get(dataTest.humidity);
        } else if (currentNode.condition == "wind") {
            currentNode = currentNode.childs.get(dataTest.wind);
        }
        findResult(currentNode, dataTest);
    }

    private static void printTree(DecisionTree tree) {
        System.out.println("Root: " + tree.root.condition);
        if (tree.root.result == null) {
            printNode(tree.root, new ArrayList<>(), new ArrayList<>());
        }
    }

    private static void printNode(NodeDecisionTree root, List<String> condBefore, List<String> typeBefore) {
        for (Map.Entry<String, NodeDecisionTree> entry : root.childs.entrySet()) {
            if (entry.getValue().result != null) {
                for (int i = 0; i < condBefore.size(); i++) {
                    System.out.print(condBefore.get(i) + ": " + typeBefore.get(i) + ", ");
                }
                System.out.println(
                        entry.getValue().condition + ": " + entry.getValue().type + " -> " + entry.getValue().result);
            } else {
                List<String> conditionBefore = new ArrayList<>();
                List<String> typesBefore = new ArrayList<>();
                conditionBefore.addAll(condBefore);
                typesBefore.addAll(typeBefore);
                conditionBefore.add(root.condition);
                typesBefore.add(entry.getKey());
                printNode(entry.getValue(), conditionBefore, typesBefore);
            }
        }
    }

    private static DecisionTree splitTree(DecisionTree tree, List<DataRow> dataTraining) {
        List<String> condBefore = new ArrayList<>();
        List<String> typeBefore = new ArrayList<>();
        NodeDecisionTree root = new NodeDecisionTree();
        tree = new DecisionTree(root);

        root = calcNode(root, dataTraining, condBefore, typeBefore);
        return tree;
    }

    private static NodeDecisionTree calcNode(NodeDecisionTree root, List<DataRow> dataTraining, List<String> condBefore,
            List<String> typeBefore) {
        if (root.result != null) {
            return root;
        }
        double gainOutlook = -1;
        double gainTemperature = -1;
        double gainHumidity = -1;
        double gainWind = -1;

        if (!checkConditionBefore("outlook", condBefore)) {
            gainOutlook = calcEntropy(dataTraining, "outlook");
        }
        if (!checkConditionBefore("temperature", condBefore)) {
            gainTemperature = calcEntropy(dataTraining, "temperature");
        }
        if (!checkConditionBefore("humidity", condBefore)) {
            gainHumidity = calcEntropy(dataTraining, "humidity");
        }
        if (!checkConditionBefore("wind", condBefore)) {
            gainWind = calcEntropy(dataTraining, "wind");
        }

        double min = -1;
        String cond = "";
        if (gainOutlook != -1) {
            min = gainOutlook;
            cond = "outlook";
            root.condition = "outlook";
        } else if (gainTemperature != -1) {
            min = gainTemperature;
            cond = "temperature";
            root.condition = "temperature";
        } else if (gainHumidity != -1) {
            min = gainHumidity;
            cond = "humidity";
            root.condition = "humidity";
        } else if (gainWind != -1) {
            min = gainWind;
            cond = "wind";
            root.condition = "wind";
        }

        if (min > gainOutlook && gainOutlook != -1) {
            min = gainOutlook;
            cond = "outlook";
            root.condition = "outlook";
        }
        if (min > gainTemperature && gainTemperature != -1) {
            min = gainTemperature;
            cond = "temperature";
            root.condition = "temperature";
        }
        if (min > gainHumidity && gainHumidity != -1) {
            min = gainHumidity;
            cond = "humidity";
            root.condition = "humidity";
        }
        if (min > gainWind && gainWind != -1) {
            min = gainWind;
            cond = "wind";
            root.condition = "wind";
        }

        Set<String> types = getAllTypeOfCondition(dataTraining, cond);
        for (String type : types) {
            List<DataRow> tempData = new ArrayList<>();
            tempData.addAll(dataTraining);
            tempData = filterData(tempData, cond, type, condBefore, typeBefore);

            List<String> conditionBefore = new ArrayList<>();
            List<String> typesBefore = new ArrayList<>();
            conditionBefore.addAll(condBefore);
            typesBefore.addAll(typeBefore);

            NodeDecisionTree n = null;
            n = createNode(n, cond, type, tempData);
            calcNode(n, tempData, conditionBefore, typesBefore);
            root.addChildNode(n, type);
        }
        return root;
    }

    private static boolean checkConditionBefore(String condition, List<String> condBefore) {
        for (String cond : condBefore) {
            if (condition == cond) {
                return true;
            }
        }
        return false;
    }

    private static List<DataRow> filterData(List<DataRow> dataTraining, String cond, String type,
            List<String> condBefore, List<String> typeBefore) {
        if (condBefore != null && typeBefore != null) {
            for (int i = 0; i < condBefore.size(); i++) {
                if (condBefore.get(i) == "outlook") {
                    for (int j = 0; j < dataTraining.size(); j++) {
                        if (dataTraining.get(j).outlook != typeBefore.get(i)) {
                            dataTraining.remove(j);
                            j--;
                        }
                    }
                } else if (condBefore.get(i) == "temperature") {
                    for (int j = 0; j < dataTraining.size(); j++) {
                        if (dataTraining.get(j).temperature != typeBefore.get(i)) {
                            dataTraining.remove(j);
                            j--;
                        }
                    }
                } else if (condBefore.get(i) == "humidity") {
                    for (int j = 0; j < dataTraining.size(); j++) {
                        if (dataTraining.get(j).humidity != typeBefore.get(i)) {
                            dataTraining.remove(j);
                            j--;
                        }
                    }
                } else if (condBefore.get(i) == "wind") {
                    for (int j = 0; j < dataTraining.size(); j++) {
                        if (dataTraining.get(j).wind != typeBefore.get(i)) {
                            dataTraining.remove(j);
                            j--;
                        }
                    }
                }
            }
        }

        if (cond == "outlook") {
            for (int i = 0; i < dataTraining.size(); i++) {
                if (dataTraining.get(i).outlook != type) {
                    dataTraining.remove(i);
                    i--;
                }
            }
        }
        if (cond == "temperature") {
            for (int i = 0; i < dataTraining.size(); i++) {
                if (dataTraining.get(i).temperature != type) {
                    dataTraining.remove(i);
                    i--;
                }
            }
        }
        if (cond == "humidity") {
            for (int i = 0; i < dataTraining.size(); i++) {
                if (dataTraining.get(i).humidity != type) {
                    dataTraining.remove(i);
                    i--;
                }
            }
        }
        if (cond == "wind") {
            for (int i = 0; i < dataTraining.size(); i++) {
                if (dataTraining.get(i).wind != type) {
                    dataTraining.remove(i);
                    i--;
                }
            }
        }
        return dataTraining;
    }

    private static NodeDecisionTree createNode(NodeDecisionTree n, String condition, String type,
            List<DataRow> dataTraining) {
        n = new NodeDecisionTree(condition);
        if (checkData(dataTraining)) {
            n.result = dataTraining.get(0).result;
            n.type = type;
        }
        return n;
    }

    private static boolean checkData(List<DataRow> dataTraining) {
        if (dataTraining.size() <= 1) {
            return true;
        }
        String result = dataTraining.get(0).result;
        for (int i = 1; i < dataTraining.size(); i++) {
            if (result != dataTraining.get(i).result) {
                return false;
            }
        }
        return true;
    }

    private static Set<String> getAllTypeOfCondition(List<DataRow> dataTraining, String cond) {
        Set<String> types = new HashSet<String>();
        for (DataRow row : dataTraining) {
            if (cond == "outlook") {
                types.add(row.outlook);
            } else if (cond == "temperature") {
                types.add(row.temperature);
            } else if (cond == "humidity") {
                types.add(row.humidity);
            } else if (cond == "wind") {
                types.add(row.wind);
            }
        }
        return types;
    }

    private static double entropy(int yes, int no, int total) {
        if (yes == 0 || no == 0) {
            return 0;
        }
        return -((double) yes / total) * Math.log((double) yes / total)
                - ((double) no / total) * Math.log((double) no / total);
    }

    private static double calcEntropy(List<DataRow> dataTraining, String condition) {
        double gain = 0;
        Set<String> types = getAllTypeOfCondition(dataTraining, condition);
        for (String type : types) {
            int yes = countYesNo(dataTraining, condition, type, "yes");
            int no = countYesNo(dataTraining, condition, type, "no");
            int total = yes + no;
            double entropy = entropy(yes, no, total);
            gain += ((double) total / dataTraining.size() * entropy);
        }
        return gain;
    }

    private static int countYesNo(List<DataRow> dataTraining, String condition, String type, String result) {
        int count = 0;
        for (DataRow row : dataTraining) {
            if (condition == "outlook") {
                if (row.outlook == type && row.result == result)
                    count++;
            } else if (condition == "temperature") {
                if (row.temperature == type && row.result == result)
                    count++;
            } else if (condition == "humidity") {
                if (row.humidity == type && row.result == result)
                    count++;
            } else if (condition == "wind") {
                if (row.wind == type && row.result == result)
                    count++;
            }
        }
        return count;
    }

}
