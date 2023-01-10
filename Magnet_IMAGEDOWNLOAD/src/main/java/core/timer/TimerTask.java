package core.timer;

public class TimerTask {
    Thread thread;
    Timer timer;
    public TimerTask(Timer timer,long m) {
        this.timer=timer;
       task(m);
    }
    public void start(){
        if (thread==null) task(1000L);

        thread.start();

    }
    public void stop(){
        thread.stop();
        thread=null;
    }
    private void task(Long m){
        if (timer!=null){
            thread = new Thread(()->{
                while (true){
                    timer.run();
                    try {
                        Thread.sleep(m);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

