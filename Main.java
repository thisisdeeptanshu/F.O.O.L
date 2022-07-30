import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<Item> items = new ArrayList<Item>();
    static ArrayList<Item> localItems = new ArrayList<Item>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean working = true;
        try {
            while (working) {
                System.out.print(">>> ");
                String command = sc.nextLine();
                String[] commands = reverse(split(command));
                working = operate(commands);
            }
        } catch (Exception e) {
            System.err.println("Nani the fuck?");
        }
    }

    static String[] split(String s) {
        ArrayList<String> sList = new ArrayList<String>();
        boolean inDoubleQuotes = false;
        boolean inBrackets = false;
        String word = "";
        for (char c : s.toCharArray()) {
            if (c == '\"' || c == '\'') inDoubleQuotes = !inDoubleQuotes;
            if (c == '(' || c == ')') inBrackets = !inBrackets;
            if (c == ' ' && !inDoubleQuotes && !inBrackets) {
                sList.add(word);
                word = "";
            } else {
                word += c;
            }
        }
        sList.add(word);
        return sList.toArray(new String[sList.size()]);
    }

    static String[] reverse(String[] strings) {
        String[] strings2 = new String[strings.length];
        int j = 0;
        for (int i = strings.length-1; i > -1; i--) {
            strings2[j] = strings[i];
            j++;
        }
        return strings2;
    }

    static boolean isAssignment(String command, Operators op) {
        if (command.length() == 1) {
            if (op.getOperator(command.toCharArray()[0]) == Operators.operators.EQUAL_TO) return true;
        }
        return false;
    }

    static boolean isInt(char[] charArr) {
        for (char c : charArr) {
            int d = Character.getNumericValue(c);
            if (d > 9 || d < 0) return false;
        }
        return true;
    }

    static boolean surroundedWithBrackets(char[] charArr) {
        int opening = 0;
        int closing = 0;
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '(') opening = i;
            else if (charArr[i] == ')') closing = i;
        }
        return opening < closing;
    }

    static String[] getFunctionInfo(char[] charArrArg) {
        String name, arg;
        char[] charArr = charArrArg;
        do {
            arg = "";
            name = "";
            int bracketsLevel = 0;
            boolean inside = false;
            for (char c : charArr) {
                if (c == '(') {
                    arg += c;
                    inside = true;
                    bracketsLevel++;
                    continue;
                }
                else if (c == ')') {
                    arg += c;
                    bracketsLevel--;
                    if (bracketsLevel == 0) break;
                }
                if (!inside) name += c;
                else arg += c;
            }
            charArr = arg.substring(1, arg.length() - 1).toCharArray();
        } while (surroundedWithBrackets(charArr));
        return new String[] {name, arg};
    }

    static String handleFunction(String name, String arg) {
        if (name.equals("print")) {
            char[] commandsCharArr = arg.toCharArray();
            if (commandsCharArr[0] == '(' && commandsCharArr[commandsCharArr.length - 1] == ')') arg = arg.substring(1, arg.length() - 1);
            commandsCharArr = arg.toCharArray();
            if (((commandsCharArr[0] == '\"' && commandsCharArr[commandsCharArr.length - 1] == '\"') || (commandsCharArr[0] == '\'' && commandsCharArr[commandsCharArr.length - 1] == '\'')) && split(arg).length == 1) {
                System.out.println(arg.substring(1, commandsCharArr.length - 1));
            }
            else if (isInt(commandsCharArr)) System.out.println(arg);
            else {
                if (split(arg).length > 1) {
                    String[] _split = split(arg);
                    String[] args = new String[2 + _split.length];
                    args[0] = "_tempitem_"; args[1] = "="; for (int i = 2; i < _split.length + 2; i++) {args[i] = _split[i-2];}
                    operate(reverse(args));
                    arg = "_tempitem_";
                    localItems.clear();
                }
                boolean worked = false;
                for (Item i : items) {
                    if (i.name.equals(arg)) {
                        System.out.println(i.value);
                        worked = true;
                        break;
                    }
                }
                if (!worked) System.err.println("Variable not found.");
            }
            return null;
        } else if (name.equals("type")) {
            char[] commandsCharArr = arg.toCharArray();
            if ((commandsCharArr[0] == '\"' && commandsCharArr[commandsCharArr.length - 1] == '\"') || (commandsCharArr[0] == '\'' && commandsCharArr[commandsCharArr.length - 1] == '\''))
                System.out.println("STRING");
            else if (isInt(commandsCharArr)) System.out.println("INT");
            else {
                if (commandsCharArr[0] == '(' && commandsCharArr[commandsCharArr.length - 1] == ')') arg = arg.substring(1, arg.length() - 1);
                boolean worked = false;
                for (Item i : items) {
                    if (i.name.equals(arg)) {
                        System.out.println(i.type.name());
                        worked = true;
                        break;
                    }
                }
                if (!worked) System.err.println("Variable not found.");
            }
            return null;
        } else if (name.equals("input")) {
            System.out.print(arg.substring(1, arg.length() - 1));
            Scanner sc = new Scanner(System.in);
            return "\"" + sc.nextLine() + "\"";
        } else if (name.equals("int")) {
            if (arg.toCharArray()[0] == '(' && arg.toCharArray()[arg.length() - 1] == ')') arg = arg.substring(1, arg.length() - 1);
            char c = arg.toCharArray()[arg.toCharArray().length - 1];
            if ((arg.toCharArray()[0] == '\"' && c == '\"') || (arg.toCharArray()[0] == '\'' && c == '\'')) return arg.substring(1, arg.length() - 1);
            else if (isInt(arg.toCharArray())) return arg;
            else {
                for (Item i : items) {
                    if (i.name.equals(arg)) {
                        return i.value;
                    }
                }
            }
            return null;
        }
        return null;
    }

    static String getValue(String s) {
        for (Item i : items) {
            if (i.name.equals(s)) {
                return i.value;
            }
        }
        return null;
    }

    static boolean operate(String[] commands) {
        Operators op = new Operators();
        int i = 0;
        Operators.operators next = Operators.operators.NONE;

        boolean working = true;
        for (String command : commands) {
            i += 1;
            if (op.getOperator(command.toCharArray()[0]) == Operators.operators.NONE) {
                char[] commandsCharArr = command.toCharArray();
                boolean doNext = true;
                while (surroundedWithBrackets(commandsCharArr) && doNext) {
                    String name = getFunctionInfo(commandsCharArr)[0];
                    String args = getFunctionInfo(commandsCharArr)[1];
                    String result = handleFunction(name, args);
                    if (result != null) {
                        command = command.replace(name + args, result);
                        commandsCharArr = command.toCharArray();
                    } else doNext = false;
                }
                if (doNext) {
                    if ((commandsCharArr[0] == '\"' && commandsCharArr[commandsCharArr.length - 1] == '\"') || (commandsCharArr[0] == '\'' && commandsCharArr[commandsCharArr.length - 1] == '\'')) localItems.add(new Item(Item.types.STRING, command.substring(1, commandsCharArr.length - 1), "temp"));
                    else if (isInt(commandsCharArr)) localItems.add(new Item(Item.types.INT, command, "temp"));
                    else localItems.add(new Item(isInt(getValue(command).toCharArray()) ? Item.types.INT : Item.types.STRING, getValue(command), "temp"));
                }
            }
            if (next == Operators.operators.NONE) next = op.getOperator((command.toCharArray()[0]));
            else {
                if (next == Operators.operators.PLUS) {
                    if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.STRING) localItems.add(new Item(Item.types.STRING, localItems.get(localItems.size() - 1).value + localItems.get(localItems.size() - 2).value, "temp"));
                    else if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item(Item.types.INT, String.valueOf(Integer.valueOf(localItems.get(localItems.size() - 1).value) + Integer.valueOf(localItems.get(localItems.size() - 2).value)), "temp"));
                    else {
                        System.err.println("Values have to be of the same type.");
                        working = false;
                        break;
                    }
                    next = Operators.operators.NONE;
                } else if (next == Operators.operators.MINUS) {
                    if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item(Item.types.INT, String.valueOf(Integer.valueOf(localItems.get(localItems.size() - 1).value) - Integer.valueOf(localItems.get(localItems.size() - 2).value)), "temp"));
                    else {
                        System.err.println("Values have to be of type int.");
                        working = false;
                        break;
                    }
                    next = Operators.operators.NONE;
                }
            }
            if (isAssignment(command, op)) {
                i--;
                if (commands.length - i != 2) {
                    System.err.println("Only one value on right side allowed.");
                    working = false;
                    break;
                }
                if (getValue(commands[commands.length - 1]) != null) {
                    int deletionIndex = 1000;
                    for (Item item : items) if (item.name == commands[commands.length - 1]) deletionIndex = items.indexOf(item);
                    items.remove(deletionIndex);
                }
                items.add(new Item(localItems.get(localItems.size() - 1).type, localItems.get(localItems.size() - 1).value, commands[commands.length - 1]));
            }
        }
        localItems.clear();
        return working;
    }
}
