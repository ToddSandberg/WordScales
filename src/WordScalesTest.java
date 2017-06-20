
import javax.swing.JOptionPane;

public class WordScalesTest {
    public static void main(String [] args){
        WordScales w = new WordScales();
        JOptionPane p = new JOptionPane();
        String s = JOptionPane.showInputDialog("enter a word");
        String pos = JOptionPane.showInputDialog("enter a part of speech");
        //System.out.println(s);
        //w.create(s, pos);
        w.writeDataFromGraph(s,pos);
        //System.out.println(w.createHashMaps(s, pos));
        //System.out.println(w.checkLists(s, pos).toString());
        //w.getSynonyms(s,pos);
        //System.out.println(s+": " + w.getSynonyms(s,pos).toString());
    }
}
