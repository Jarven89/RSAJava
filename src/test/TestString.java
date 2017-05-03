package test;

/**
 * Created by hyshi on 01/05/2017.
 */
public class TestString {
    String str = new String("good");
    char [] bs = {'a','b','c'};
    public void change(String str,char[] bs){
        str = "test ok";
        bs[0] = 'c';

    }

    public static void main(String[] args) {
       TestString ts =  new TestString();
       ts.change(ts.str,ts.bs);
        System.out.println(ts.str+"and");
        System.out.print(ts.bs);
    }
}
