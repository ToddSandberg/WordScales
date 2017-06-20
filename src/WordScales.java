
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.dictionary.Dictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class WordScales {
    Dictionary dictionary;
    String PartOfSpeech = "";
    ArrayList<String> words = new ArrayList<String>();

    public WordScales() {
        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    public String getWord(String s, String pos) {
        try {
            boolean write = false; //used to determine if the character is part of a word
            IndexWord word = null;
            //finds the correct part of speech
            pos = pos.toLowerCase();
            if (pos.equals("noun")) {
                word = dictionary.lookupIndexWord(POS.NOUN, s);
                PartOfSpeech = "Noun";
            }
            else if (pos.equals("adjective") || pos.equals("adj")) {
                word = dictionary.lookupIndexWord(POS.ADJECTIVE, s);
                PartOfSpeech = "Adjective";
            }
            else if (pos.equals("verb")) {
                word = dictionary.lookupIndexWord(POS.VERB, s);
                PartOfSpeech = "Verb";
            }
            else if (pos.equals("adverb") || pos.equals("adv")) {
                word = dictionary.lookupIndexWord(POS.ADVERB, s);
                PartOfSpeech = "Adverb";
            }
            String temp = "";
            //gets the hyponyms
            try {
                PointerTargetTree hyponyms = PointerUtils
                        .getHyponymTree(word.getSenses().get(0));
                // temp += "Hyponyms of \"" + word.getLemma() + "\":";
                String hypos = hyponyms.toList().toString();
                for (int x = 0; x < hypos.length() - 6; x++) {
                    if (hypos.substring(x, x + 6).equals("Words:")) {
                        x = x + 6;
                        write = true;
                    }
                    else if (hypos.charAt(x) == '(') {
                        write = false;
                    }
                    if (write) {
                        temp += hypos.charAt(x);
                    }
                }
            }
            catch (Exception e) {
                // e.printStackTrace();
            }
            //gets the synonyms
            try {
                PointerTargetNodeList synonyms = PointerUtils
                        .getSynonyms(word.getSenses().get(0));
                // temp += "Synonyms of \"" + word.getLemma() + "\":";
                String syns = synonyms.toString();
                for (int x = 0; x < syns.length() - 6; x++) {
                    if (syns.substring(x, x + 6).equals("Words:")) {
                        x = x + 6;
                        write = true;
                    }
                    else if (syns.charAt(x) == '(') {
                        write = false;
                    }
                    if (write) {
                        temp += syns.charAt(x);
                    }
                }
            }
            catch (Exception e) {
                // e.printStackTrace();
            }
            //gets the hypernyms
            try {
                PointerTargetNodeList hypernyms = PointerUtils
                        .getDirectHypernyms(word.getSenses().get(0));
                // temp += "Direct hypernyms of \"" + word.getLemma() + "\":";
                String hypers = hypernyms.toString();
                for (int x = 0; x < hypers.length() - 6; x++) {
                    if (hypers.substring(x, x + 6).equals("Words:")) {
                        x = x + 6;
                        write = true;
                    }
                    else if (hypers.charAt(x) == '(') {
                        write = false;
                    }
                    if (write) {
                        temp += hypers.charAt(x);
                    }
                }
            }
            catch (Exception e) {
                // e.printStackTrace();
            }
            //gets the seealso tab
            try {
                PointerTargetNodeList seealso = PointerUtils
                        .getAlsoSees(word.getSenses().get(0));
                // temp += "Direct hypernyms of \"" + word.getLemma() + "\":";
                String seealsos = seealso.toString();
                for (int x = 0; x < seealsos.length() - 6; x++) {
                    if (seealsos.substring(x, x + 6).equals("Words:")) {
                        x = x + 6;
                        write = true;
                    }
                    else if (seealsos.charAt(x) == '(') {
                        write = false;
                    }
                    if (write) {
                        temp += seealsos.charAt(x);
                    }
                }
            }
            catch (Exception e) {
                // e.printStackTrace();
            }

            return temp;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<String> getSynonyms(String word, String pos) {
        String s = getWord(word, pos);
        ArrayList<String> syns = new ArrayList<String>();
        String[] split = s.split(",");
        for (int x = 0; x < split.length; x++) {
            if (split[x].charAt(0) == ' ') {
                split[x] = split[x].substring(1, split[x].length());
            }
            if (split[x].contains(" -- ")) {
                String[] temp = split[x].split(" -- ");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i].length() > 0 && temp[i].charAt(0) == ' ') {
                        temp[i] = temp[i].substring(1, temp[i].length());
                    }
                    syns.add(temp[i]);
                }
            }
            else {
                syns.add(split[x]);

            }

        }
        return syns;
    }

    public ArrayList<String> getAntonyms(String word, String pos) {
        String s = getAntiWord(word, pos);
        ArrayList<String> ants = new ArrayList<String>();
        String[] split = s.split(",");
        for (int x = 0; x < split.length; x++) {
            if (split[x].length() > 0 && split[x].charAt(0) == ' ') {
                split[x] = split[x].substring(1, split[x].length());
            }
            if (split[x].contains(" -- ")) {
                String[] temp = split[x].split(" -- ");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i].length() > 0 && temp[i].charAt(0) == ' ') {
                        temp[i] = temp[i].substring(1, temp[i].length());
                    }
                    ants.add(temp[i]);
                }
            }
            else {
                ants.add(split[x]);

            }

        }

        return ants;
    }

    private String getAntiWord(String s, String pos) {
        try {
            boolean write = false;
            IndexWord word = null;
            pos = pos.toLowerCase();
            if (pos.equals("noun")) {
                word = dictionary.lookupIndexWord(POS.NOUN, s);
                PartOfSpeech = "Noun";
            }
            else if (pos.equals("adjective") || pos.equals("adj")) {
                word = dictionary.lookupIndexWord(POS.ADJECTIVE, s);
                PartOfSpeech = "Adjective";
            }
            else if (pos.equals("verb")) {
                word = dictionary.lookupIndexWord(POS.VERB, s);
                PartOfSpeech = "Verb";
            }
            else if (pos.equals("adverb") || pos.equals("adv")) {
                word = dictionary.lookupIndexWord(POS.ADVERB, s);
                PartOfSpeech = "Adverb";
            }
            String temp = "";
            try {
                PointerTargetNodeList antonyms = PointerUtils
                        .getAntonyms(word.getSenses().get(0));
                String antis = antonyms.toString();
                for (int x = 0; x < antis.length() - 6; x++) {
                    if (antis.substring(x, x + 6).equals("Words:")) {
                        x = x + 6;
                        write = true;
                    }
                    else if (antis.charAt(x) == '(') {
                        write = false;
                    }
                    if (write) {
                        temp += antis.charAt(x);
                    }
                }
            }
            catch (Exception e) {
                // e.printStackTrace();
            }
            try {
                PointerTargetTree antonyms = PointerUtils
                        .getIndirectAntonyms(word.getSenses().get(0));
                String antis = antonyms.toList().toString();
                for (int x = 0; x < antis.length() - 6; x++) {
                    if (antis.substring(x, x + 6).equals("Words:")) {
                        x = x + 6;
                        write = true;
                    }
                    else if (antis.charAt(x) == '(') {
                        write = false;
                    }
                    if (write) {
                        temp += antis.charAt(x);
                    }
                }
            }
            catch (Exception e) {
                // e.printStackTrace();
            }
            return temp;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String fixSpaces(String s) {
        String[] split = s.split(" ");
        String temp = split[0];
        for (int x = 1; x < split.length; x++) {
            temp += "_" + split[x];
        }
        split = temp.split("-");
        temp = split[0];
        for (int x = 1; x < split.length; x++) {
            temp += "_" + split[x];
        }
        split = temp.split("'");
        temp = split[0];
        for (int x = 1; x < split.length; x++) {
            temp += split[x];
        }
        split = temp.split("\\.");
        temp = split[0];
        for (int x = 1; x < split.length; x++) {
            temp += split[x];
        }

        return temp;
    }

    public ArrayList<String> checkLists(String s, String pos) {
        ArrayList<String> syns = new ArrayList<String>();
        ArrayList<String> ants = new ArrayList<String>();
        syns = getSynonyms(s, pos);
        ants = getAntonyms(s, pos);
        if (ants.size() < 1) {
            ants = getAntonyms(JOptionPane.showInputDialog(
                    "enter a target antonym for" + s + pos), pos);
        }
        ants = getSynonyms(ants.get(0), pos);
        // System.out.println(ants.toString());
        // System.out.println(syns.toString());
        for (int x = 0; x < syns.size(); x++) {
            ArrayList<String> temp = getAntonyms(syns.get(x), pos);
            boolean contains = false;
            for (int i = 0; i < temp.size(); i++) {
                if (ants.contains(temp.get(i))) {
                    contains = true;
                }
            }
            if (!contains) {
                syns.remove(x);
                x--;
            }
        }
        return syns;
    }

    public void create(String s, String pos) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:neo4j:bolt://localhost/?user=neo4j,password=legendary",
                "neo4j", "legendary")) {
            // Connect

            // Querying
            Statement stmt = con.createStatement();
            String query = "MATCH (n)\nDETACH DELETE n";
            stmt.executeQuery(query);
            stmt.close();
            con.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        createWordGraph(s, pos);
        String anti = getAntonyms(s, pos).get(0);
        if (!anti.equals("")) {
            createWordGraph(anti, pos);
            try (Connection con = DriverManager.getConnection(
                    "jdbc:neo4j:bolt://localhost/?user=neo4j,password=legendary",
                    "neo4j", "legendary")) {
                // Connect

                // Querying
                Statement stmt = con.createStatement();
                String query = "MATCH (a:Adjective { word: '" + s
                        + "' }),(b:Adjective { word: '" + anti
                        + "' }), p = allShortestPaths((a)-[*]-(b))\nRETURN size(nodes(p))\n";

                ResultSet rs = stmt.executeQuery(query);
                int x = 1;
                while (rs.next()) {
                    int lengthOfPath = rs.getInt(x);
                    System.out.println("Length of path from " + s + "to" + anti
                            + ": " + lengthOfPath);
                    System.out.println(rs.getString(x));
                    rs.next();
                    x++;
                }
                stmt.close();
                con.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            anti = JOptionPane
                    .showInputDialog("enter a target antonym for" + s + pos);
            createWordGraph(anti, pos);
            try (Connection con = DriverManager.getConnection(
                    "jdbc:neo4j:bolt://localhost/?user=neo4j,password=legendary",
                    "neo4j", "legendary")) {
                // Connect

                // Querying
                Statement stmt = con.createStatement();
                String query = "MATCH (a:Adjective { word: '" + s
                        + "' }),(b:Adjective { word: '" + anti
                        + "' }), p = allShortestPaths((a)-[*]-(b))\nRETURN size(nodes(p))\n";

                ResultSet rs = stmt.executeQuery(query);
                int x = 1;
                while (rs.next()) {
                    int lengthOfPath = rs.getInt(x);
                    System.out.println("Length of path from " + s + "to" + anti
                            + ": " + lengthOfPath);
                    rs.next();
                    x++;
                }
                stmt.close();
                con.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeDataFromGraph(String word,String pos){
        try (Connection con = DriverManager.getConnection(
                "jdbc:neo4j:bolt://localhost/?user=neo4j,password=legendary",
                "neo4j", "legendary")) {
            ArrayList<String> negfive= new ArrayList<String>(),negfour= new ArrayList<String>(),negthree= new ArrayList<String>(),negtwo= new ArrayList<String>(),negone= new ArrayList<String>(),zero= new ArrayList<String>(),one= new ArrayList<String>(),two= new ArrayList<String>(),three= new ArrayList<String>(),four= new ArrayList<String>(),five = new ArrayList<String>();
            Statement stmt = con.createStatement();
            String query ="MATCH(n:"+pos+" {word:'"+word+"'})\n"
                    + "SET n.rank = 5\n"
                    + "return n";
            stmt.execute(query);
            String antiquery ="MATCH(n:"+pos+" {word:'"+getAntonyms(word,pos).get(0)+"'})\n"
                    + "SET n.rank = -5\n"
                    + "return n";
            stmt.execute(antiquery);
            String syns = "MATCH (p:"+pos+")-[r:Synonym]->(c:"+pos+")\n"
                    + "WHERE c.word = '"+word+"' \nRETURN p.word;";
            ResultSet rs = stmt.executeQuery(syns);
            while(rs.next()){
                String syn = rs.getString(1);
                int rank = 4;
                String checkforothers = "MATCH(n:"+pos+" {word:'"+syn+"'})\n"
                        + "return n.rank";
                ResultSet chk = stmt.executeQuery(checkforothers);
                while(chk.next()){
                    try{
                    rank +=((chk.getInt(1)-rank)/2);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                String submitRank = "MATCH(n:"+pos+" {word:'"+syn+"'})\n"
                        + "SET n.rank = "+rank+"\n"
                        + "return n";
                stmt.execute(submitRank);
                
                
            }
            String ants = "MATCH (p:"+pos+")-[r:Synonym]->(c:"+pos+")\n"
                    + "WHERE c.word = '"+getAntonyms(word,pos).get(0)+"' \nRETURN p.word;";
            ResultSet antrs = stmt.executeQuery(ants);
            while(antrs.next()){
                String syn = antrs.getString(1);
                int rank = -4;
                String checkforothers = "MATCH(n:"+pos+" {word:'"+syn+"'})\n"
                        + "return n.rank";
                ResultSet chk = stmt.executeQuery(checkforothers);
                while(chk.next()){
                    rank +=((chk.getInt(1)-rank)/2);
                }
                String submitRank = "MATCH(n:"+pos+" {word:'"+syn+"'})\n"
                        + "SET n.rank = "+rank+"\n"
                        + "return n";
                stmt.execute(submitRank);
            }
            ArrayList<String> twosyns = checkLists(word,pos);
            for(int i=0;i<twosyns.size();i++){
                String synsfortwo = "MATCH (p:"+pos+")-[r:Synonym]->(c:"+pos+")\n"
                        + "WHERE c.word = '"+twosyns.get(i)+"' \nRETURN p.word;";
                //System.out.println(twosyns.get(i));
                ResultSet rstwo = stmt.executeQuery(synsfortwo);
                while(rstwo.next()){
                    String syn = rstwo.getString(1);
                    System.out.println(syn);
                    String checkrank = "MATCH(n:"+pos+" {word:'"+syn+"'})\n"
                            + "return n.rank";
                    ResultSet chkrnk = stmt.executeQuery(checkrank);
                    chkrnk.next();
                    int rank = chkrnk.getInt(1);
                    //System.out.println(rank);
                    String checkforothers = "MATCH (p:"+pos+")-[r:Synonym]->(c:"+pos+")\n"
                            + "WHERE c.word = '"+syn+"' \nRETURN p.rank;";
                    ResultSet chk = stmt.executeQuery(checkforothers);
                    while(chk.next()){
                        rank +=((chk.getInt(1)-rank)/2);
                        System.out.println(chk.getInt(1));
                    }
                    String submitRank = "MATCH(n:"+pos+" {word:'"+syn+"'})\n"
                            + "SET n.rank = "+rank+"\n"
                            + "return n";
                    stmt.execute(submitRank);
                   
                }
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void createWordGraph(String s, String pos) {

            try (Connection con = DriverManager.getConnection(
                    "jdbc:neo4j:bolt://localhost/?user=neo4j,password=legendary",
                    "neo4j", "legendary")) {
                // Connect

                // Querying
                Statement stmt = con.createStatement();
                String query = "";
                
                s = fixSpaces(s);
                if (!words.contains(s)) {
                    try {
                        query = "MERGE (" + s + ":" + pos + " {word:'" + s
                                + "'})\n";
                        query += ";";
                        stmt.executeUpdate(query);
                        words.add(s);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                query = "";
                ArrayList<String> syns = checkLists(s, pos);
                System.out.println("loading " + s+"...");
                //first synonyms
                for (int x = 0; x < syns.size(); x++) {
                    if (!syns.get(x).equals(s)) {
                        String syn = fixSpaces(syns.get(x));
                        if (!words.contains(syn)) {
                            try {
                                query = "MERGE (" + syn + ":" + pos
                                        + " {word:'" + syn + "'})\n";
                                query += ";";
                                stmt.executeUpdate(query);
                                query = "";
                                words.add(syn);

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            query = "MATCH (a:" + pos + " {word:'" + s
                                    + "'}),\n(b:" + pos + " {word:'" + syn
                                    + "'})\n";
                            query += "MERGE\n";
                            query += "  (a)-[r:Synonym]->(b)\n";
                            query += "\n;";
                            stmt.executeUpdate(query);

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        //second synonyms
                        ArrayList<String> syns2 = checkLists(syn, pos);
                        for(int i=0;i<syns2.size();i++){
                            if (!syns2.get(i).equals(syn)) {
                                String syn2 = fixSpaces(syns2.get(i));
                                if (!words.contains(syn2)) {
                                    try {
                                        query = "MERGE (" + syn2 + ":" + pos
                                                + " {word:'" + syn2 + "'})\n";
                                        query += ";";
                                        stmt.executeUpdate(query);
                                        query = "";
                                        words.add(syn2);

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    query = "MATCH (a:" + pos + " {word:'" + syn
                                            + "'}),\n(b:" + pos + " {word:'" + syn2
                                            + "'})\n";
                                    query += "MERGE\n";
                                    query += "  (a)-[r:Synonym]->(b)\n";
                                    query += "\n;";
                                    stmt.executeUpdate(query);

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //third synonyms
                                ArrayList<String> syns3 = checkLists(syn2, pos);
                                for(int p=0;p<syns3.size();p++){
                                    if (!syns3.get(p).equals(syn2)) {
                                        String syn3 = fixSpaces(syns3.get(p));
                                        if (!words.contains(syn3)) {
                                            try {
                                                query = "MERGE (" + syn3 + ":" + pos
                                                        + " {word:'" + syn3 + "'})\n";
                                                query += ";";
                                                stmt.executeUpdate(query);
                                                query = "";
                                                words.add(syn3);

                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        try {
                                            query = "MATCH (a:" + pos + " {word:'" + syn2
                                                    + "'}),\n(b:" + pos + " {word:'" + syn3
                                                    + "'})\n";
                                            query += "MERGE\n";
                                            query += "  (a)-[r:Synonym]->(b)\n";
                                            query += "\n;";
                                            stmt.executeUpdate(query);

                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //fourth synonyms
                                        ArrayList<String> syns4 = checkLists(syn3, pos);
                                        for(int c=0;c<syns4.size();c++){
                                            if (!syns4.get(c).equals(syn3)) {
                                                String syn4 = fixSpaces(syns4.get(c));
                                                if (!words.contains(syn4)) {
                                                    try {
                                                        query = "MERGE (" + syn4 + ":" + pos
                                                                + " {word:'" + syn4 + "'})\n";
                                                        query += ";";
                                                        stmt.executeUpdate(query);
                                                        query = "";
                                                        words.add(syn4);

                                                    }
                                                    catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                try {
                                                    query = "MATCH (a:" + pos + " {word:'" + syn3
                                                            + "'}),\n(b:" + pos + " {word:'" + syn4
                                                            + "'})\n";
                                                    query += "MERGE\n";
                                                    query += "  (a)-[r:Synonym]->(b)\n";
                                                    query += "\n;";
                                                    stmt.executeUpdate(query);

                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                //fifth synonyms
                                                ArrayList<String> syns5 = checkLists(syn4, pos);
                                                for(int d=0;d<syns5.size();d++){
                                                    if (!syns5.get(d).equals(syn4)) {
                                                        String syn5 = fixSpaces(syns5.get(d));
                                                        if (!words.contains(syn5)) {
                                                            try {
                                                                query = "MERGE (" + syn5 + ":" + pos
                                                                        + " {word:'" + syn5 + "'})\n";
                                                                query += ";";
                                                                stmt.executeUpdate(query);
                                                                query = "";
                                                                words.add(syn5);

                                                            }
                                                            catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        try {
                                                            query = "MATCH (a:" + pos + " {word:'" + syn4
                                                                    + "'}),\n(b:" + pos + " {word:'" + syn5
                                                                    + "'})\n";
                                                            query += "MERGE\n";
                                                            query += "  (a)-[r:Synonym]->(b)\n";
                                                            query += "\n;";
                                                            stmt.executeUpdate(query);

                                                        }
                                                        catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                stmt.close();
                con.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    
    /**
     * creates a hashmap for synonyms and a hashmap for antonyms and compares their values
     * @param s the word to get comparisons for
     * @param pos the part of speech of a word
     * @return the middle of the word
     */
    public String createHashMaps(String s, String pos) {
        /**
         * Declarations
         */
        //All matching words with their position in both lists
        HashMap<String,String> results = new HashMap<String,String>();
        //synonyms hashmap
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> syns = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();
        //antonyms hashmap
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> ants = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();
        /**
         * make hashmap for synonyms of s
         */
        ArrayList<String> temp = getSynonyms(s, pos);
        HashMap<String, HashMap<String, ArrayList<String>>> one = new HashMap<String, HashMap<String, ArrayList<String>>>();
        for (int x = 0; x < temp.size(); x++) {
            HashMap<String, ArrayList<String>> two = new HashMap<String, ArrayList<String>>();
            ArrayList<String> onetemp = getSynonyms(temp.get(x), pos);
            for (int i = 0; i < onetemp.size(); i++) {
                ArrayList<String> three = getSynonyms(onetemp.get(i), pos);
                two.put(onetemp.get(i), three);
            }
            one.put(temp.get(x), two);
        }
        syns.put(s, one);
        /**
         * make hashmap for antonyms of s
         */
        String ant = getAntonyms(s, pos).get(0);
        ArrayList<String> tempant = getSynonyms(ant, pos);
        HashMap<String, HashMap<String, ArrayList<String>>> oneant = new HashMap<String, HashMap<String, ArrayList<String>>>();
        for (int x = 0; x < tempant.size(); x++) {
            HashMap<String, ArrayList<String>> two = new HashMap<String, ArrayList<String>>();
            ArrayList<String> onetemp = getSynonyms(tempant.get(x), pos);
            for (int i = 0; i < onetemp.size(); i++) {
                ArrayList<String> three = getSynonyms(onetemp.get(i), pos);
                two.put(onetemp.get(i), three);
            }
            oneant.put(tempant.get(x), two);
        }
        ants.put(ant, oneant);
        /**
         * finds strings contained in both
         */
        //puts antonyms in a string
        String finalstring = "";
        Iterator<?> it = ants.entrySet().iterator();
        String antonyms = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            antonyms = pair.getKey() + " = " + pair.getValue();
        }
        //System.out.println(antonyms);
        //checks every part of synonyms and compares it to the antonyms string
        Iterator<?> iter = syns.get(s).entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            String onedeep = (String) pair.getKey();
            if (antonyms.contains(" " + (CharSequence) pair.getKey() + "=") || antonyms.contains(" " + (CharSequence) pair.getKey() + "]") || antonyms.contains(" " + (CharSequence) pair.getKey() + ",")) {
                results.put(onedeep, "order: "+s);
            }
            Iterator<?> iterone = ((HashMap<String, ArrayList<String>>) pair
                    .getValue()).entrySet().iterator();
            while (iterone.hasNext()) {
                Map.Entry pairthree = (Map.Entry) iterone.next();
                String twodeep = (String) pairthree.getKey();
                if (antonyms.contains(" " + (CharSequence) pairthree.getKey() + "=") || antonyms.contains(" " + (CharSequence) pairthree.getKey() + "]") || antonyms.contains(" " + (CharSequence) pairthree.getKey() + ",")) {
                    results.put(twodeep,"order: "+s+"->"+onedeep);
                }
                ArrayList<String> al = (ArrayList<String>) pairthree
                        .getValue();
                for (int x = 0; x < al.size(); x++) {
                    if (antonyms.contains(" " + al.get(x) + "=")|| antonyms.contains(" " + al.get(x) + "]") || antonyms.contains(" " + al.get(x) + ",")) {
                        results.put(al.get(x) , "order:"+s+"->"+onedeep+"->"+twodeep);
                    }
                }
                iterone.remove();
            }
            iter.remove();
        }
        /**
         * check for the position in antonyms
         */
        System.out.println(results.toString());
        Iterator<?> iterant = ants.get(ant).entrySet().iterator();
        while (iterant.hasNext()) {
            Map.Entry pair = (Map.Entry) iterant.next();
            String onedeep = (String) pair.getKey();
            //System.out.println(onedeep +":"+results.containsKey(onedeep));
            if (results.containsKey(onedeep) == true) {
                //System.out.println(onedeep +":"+results.get(onedeep));
                String stuff = results.get(onedeep);
                //System.out.println("onedeep:"+stuff);
                results.put("|"+onedeep,stuff +"->"+onedeep +"->"+ant);
            }
            Iterator<?> iterone = ((HashMap<String, ArrayList<String>>) pair
                    .getValue()).entrySet().iterator();
            while (iterone.hasNext()) {
                Map.Entry pairthree = (Map.Entry) iterone.next();
                String twodeep = (String) pairthree.getKey();
                //System.out.println(twodeep +":"+results.containsKey(twodeep));
                if (results.containsKey(twodeep) == true) {
                    String stuff = results.get(twodeep);
                    //System.out.println("twodeep:"+stuff);
                    results.put("|"+twodeep,stuff+"->"+twodeep+"->"+onedeep +"->"+ant);
                }
                ArrayList<String> al = (ArrayList<String>) pairthree
                        .getValue();
                for (int x = 0; x < al.size(); x++) {
                    //System.out.println(al.get(x) +":"+results.containsKey(al.get(x)));
                    if (results.containsKey(al.get(x)) == true) {
                        String stuff = results.get(al.get(x));
                        //System.out.println("threedeep:"+stuff);
                        results.put("|"+al.get(x) , stuff+"->"+al.get(x)+"->"+twodeep+"->"+onedeep+"->"+ant);
                    }
                }
            }
        }
        return results.toString();
    }
    /*
     * public int getScale(String s, String pos){ //removeMorphology(s); String
     * temp = getWord(s,pos); int rank = 0;
     * if(temp.contains("extremely")||temp.contains("intense")||temp.contains(
     * "exceedingly")||temp.contains("very")){ rank = 2; } else
     * if(temp.contains("high")||temp.contains("above average")){ rank = 1; }
     * else if(temp.contains("moderate") ||
     * temp.contains("moderately")||temp.contains("average")){ rank = 0; }
     * if(temp.contains("low") ||
     * temp.contains("below average")||temp.contains("not")){ if(rank==2){ rank
     * = -2; } else{ rank = -1; } } return rank; }
     */
}
