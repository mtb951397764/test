public class Test implements Cloneable {
    public String a;
    native public void ss();


    @org.junit.Test
    public void go()
    {

        String str = "  FORMOTHERCCCPRUSSIA";
        System.out.println(str.contains("CCCPA"));

    }

    @org.junit.Test
    public void go2() throws CloneNotSupportedException {
        new Test().start();
        Test test = new Test();
        test.a = "ccc";
        Test obj = (Test) test.clone();
        System.out.println(obj.a);
    }
    public void start()
    {
        new Test()
        {
            public void ci()
            {
                System.out.println("test");
            }
        }.ci();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("C");
            }
        }).start();
        new Thread().start();

    }


}



