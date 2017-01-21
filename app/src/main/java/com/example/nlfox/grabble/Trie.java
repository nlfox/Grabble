package com.example.nlfox.grabble;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by nlfox on 1/14/17.
 */

public class Trie {
    class Node {
        Map<Character, Node> children;

        Node() {
            children = new HashMap<>();
        }

        public Node addChild(Character c) {
            if (children.containsKey(c)) {
                return children.get(c);
            } else {
                children.put(c, new Node());
                return children.get(c);
            }
        }

        public Node getChild(Character c) {
            if (children.containsKey(c)) {
                return children.get(c);
            } else {
                return null;
            }
        }

    }

    Node head;

    Trie() {
        head = new Node();
    }

    Trie(String s) {
        head = deserialize(s);
    }


    Trie(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        head = deserialize((s.hasNext() ? s.next() : "").trim());
    }


    void addString(String s) {
        s = s.toLowerCase();
        Node node = head;
        for (Character i : s.toCharArray()) {
            node = node.addChild(i);
        }
    }

    boolean validWord(String s) {
        if (s.length() != 7) {
            return false;
        }
        Node node = head;
        for (Character i :
                s.toCharArray()) {
            node = node.getChild(i);
            if (node == null) {
                return false;
            }
        }
        if (node.children.size() == 0) {
            return true;
        }
        return false;

    }

    /**
     * This method will be invoked first, you should design your own algorithm
     * to serialize a trie which denote by a root node to a string which
     * can be easily deserialized by your own "deserialize" method later.
     */
    public String serialize() {
        return serialize(head);
    }

    public String serialize(Node root) {
        // Write your code here
        if (root == null)
            return "";

        StringBuffer sb = new StringBuffer();
        sb.append("<");
        Iterator iter = root.children.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Character key = (Character) entry.getKey();
            Node child = (Node) entry.getValue();
            sb.append(key);
            sb.append(serialize(child));
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * This method will be invoked second, the argument data is what exactly
     * you serialized at method "serialize", that means the data is not given by
     * system, it's given by your own serialize method. So the format of data is
     * designed by yourself, and deserialize it here as you serialize it in
     * "serialize" method.
     */
    public Node deserialize(String data) {
        // Write your code here
        if (data == null || data.length() == 0)
            return null;

        Node root = new Node();
        Node current = root;
        Stack<Node> path = new Stack<Node>();
        for (Character c : data.toCharArray()) {
            switch (c) {
                case '<':
                    path.push(current);
                    break;
                case '>':
                    path.pop();
                    break;
                default:
                    current = new Node();
                    path.peek().children.put(c, current);
            }
        }

        return root;
    }

    private Node getNode(String s) {
        Node n = head;

        for (Character i :
                s.toCharArray()) {
            if (n.children.containsKey(i)) {
                n = n.getChild(i);
            } else {
                return null;
            }
        }


        return n;
    }

    int cnt = 0;
    List<String> l;

    public List<String> getSuggestion(String s) {
        l = new ArrayList<>();
        cnt = 0;
        Node n = getNode(s);
        if (n != null) {
            dfs(s, n);
        } else {
            return l;
        }

        return l;
    }

    private void dfs(String s, Node n) {
        if (cnt == 2) {
            return;
        }
        if (n.children.size() == 0) {
            l.add(s);
            cnt += 1;
        }
        for (Map.Entry<Character, Node> i : n.children.entrySet()) {
            dfs((s + i.getKey().toString()), i.getValue());
        }
    }


//    public static void main(String[] args) {
//        Trie t = new Trie();
//        //
//
//        System.out.println(t.serialize()); // Display the string.
//
//    }

}
