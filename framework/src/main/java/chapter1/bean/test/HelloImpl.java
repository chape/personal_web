package chapter1.bean.test;

/**
 * Created by ChaoChao on 11/12/2017.
 */
public class HelloImpl implements Hello{
    @Override
    public void say(String name){
        System.out.println("Hello, " + name);
    }
}
