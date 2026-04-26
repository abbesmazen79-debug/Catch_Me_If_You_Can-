package game;


public class GameLoop implements Runnable {

    private Game    game;
    private Thread  thread;
    private boolean running;

    public GameLoop(Game game) {
        this.game = game;
        
    }

    public void start() {
        running = true;
        thread  = new Thread(this, "GameLoop");
        thread.start();
    }

    public void stop() {
        running = false;
        try { thread.join(500); } catch (InterruptedException ignored) {}
    }

    //@Override
    public void run() {
        long lastTime  = System.nanoTime();
        long timer     = System.currentTimeMillis();
        int  frames    = 0;
        double delta   = 0;
        double nsPerTick = 1_000_000_000.0 / 60.0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                game.update();
                delta--;
            }

            game.render();
            frames++;

            if (System.currentTimeMillis() - timer >= 1000) {
                timer += 1000;
                frames = 0;
            }

            // Cap to ~60 fps
            long sleep = (long)((lastTime - System.nanoTime() + nsPerTick) / 1_000_000);
            if (sleep > 0) {
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            }
        }
    }
}