package game;
import city.cs.engine.*;
import javax.swing.*;
import java.awt.*;

//to do :
//
//      text check - spelling etc.
//      <2min video
//
//
    //



//calls main game
//show menu>level1>level2>level3>show win screen.

public class Game implements LevelCompleteListener {  //level listnener so it can switch between levels

    //custom images per level, all defined priv locally so can override
    private static final String PLAYER_IMG = "data/playerNoBKG.png";
    private static final String DONKEY_IMG = "data/homelessNoBKG.png";
    private static final String PRINCESS_IMG = "data/adil_in_distressNoBKG.png";
    private static final String BARREL_IMG = "data/barrelNoBKG.png";
    private static final String BKG_IMG = "data/skyscraperBKG.PNG";


    private JFrame frame;
    private PlayerController controller;
    private World  currentWorld;
    private JPanel currentView;
    private int totalScore = 0;

    //single shared SoundPlayer — persists across levels so volume stays the same
    private final SoundPlayer soundPlayer = new SoundPlayer();

    public Game() {  //game init, title resise etc then calls mainmenu.
        frame = new JFrame("Dodge and Dash!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        showMainMenu();
        frame.setVisible(true);

    }

    //main menu - this shows before game
    private void showMainMenu() {
        if (currentWorld != null) { currentWorld.stop(); currentWorld = null; }
        if (controller != null) {
            frame.removeKeyListener(controller);
            frame.removeMouseListener(controller.mouseAdapter);
            if (currentView != null) currentView.removeKeyListener(controller);
            if (currentView != null) currentView.removeMouseListener(controller.mouseAdapter);
            controller = null;
        }


        //init menu w splayer + lambda to start each level - lambdas from a level cw
        MainMenu menu = new MainMenu(soundPlayer, this::startLevel1, //method reference, no params needed
                this::startLevel1,  //start at level 1 , but also have strt level 1 buton below
                () -> startLevel2(5),  //start each level w 5 lives
                () -> startLevel3(5), //lambda, () means no params passed
                () -> startLevel4(5)); //-> means run startLevel4 w 5 lives passsed in

        //lambsda like a function you can pass as a varibale - if something done -> run this


        if (currentView != null) frame.remove(currentView);  //remove current view +
        currentView = menu;                                     //switch current view to menu
        frame.add(menu);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }

    //level starting methods
    private void startLevel1() {
        Level1 world = new Level1(PLAYER_IMG, DONKEY_IMG, PRINCESS_IMG, BARREL_IMG, BKG_IMG, soundPlayer); //calls lvl1 w needed params passed in
        world.setLevelCompleteListener(this); //sets listenter to focus on level1
        Level1View view = new Level1View(
                world, 800, 800,
                this::showMainMenu,
                totalScore,
                soundPlayer,
                this::startLevel1,  //start at level 1 , but also have strt level 1 buton below
                () -> startLevel2(5),  //start each level w 5 lives
                () -> startLevel3(5), //lambda, () means no params passed
                () -> startLevel4(5)//-> means run startLevel4 w 5 lives passsed in
        );


        swapLevel(world, view, world.getPlayer());
        //DebugViewer dv = new DebugViewer(world, 900, 900);  //debug viewer back, fixed finally
        //dv.setLocation(820, 0);
        world.start();  //starts game
    }

    private void startLevel2(int carryLives) {  //same as above but for level 2
        Level2 world = new Level2(PLAYER_IMG, DONKEY_IMG, PRINCESS_IMG, BARREL_IMG, soundPlayer, BKG_IMG, carryLives);
        world.setLevelCompleteListener(this);
        Level2View view = new Level2View(
                world, 800, 800,
                this::showMainMenu,
                totalScore,
                soundPlayer,
                this::startLevel1,  //start at level 1 , but also have strt level 1 buton below
                () -> startLevel2(5),  //start each level w 5 lives
                () -> startLevel3(5), //lambda, () means no params passed
                () -> startLevel4(5)//-> means run startLevel4 w 5 lives passsed in
        );


        swapLevel(world, view, world.getPlayer());
        //DebugViewer dv = new DebugViewer(world, 900, 900);  //debug viewer back, fixed finally
        //dv.setLocation(820, 0);
        world.start();
    }



    private void startLevel3(int carryLives) {
        Level3 world = new Level3(PLAYER_IMG, DONKEY_IMG, PRINCESS_IMG, BARREL_IMG, BKG_IMG, carryLives, soundPlayer);
        world.setLevelCompleteListener(this);
        Runnable goToLevel4 = () -> startLevel4(world.getPlayer().getLives());
        Level3View view = new Level3View(
                world, 800, 800,
                this::showMainMenu,
                totalScore,
                soundPlayer,
                this::startLevel1,  //start at level 1 , but also have strt level 1 buton below
                () -> startLevel2(5),  //start each level w 5 lives
                () -> startLevel3(5), //lambda, () means no params passed
                () -> startLevel4(5)//-> means run startLevel4 w 5 lives passsed in
        );


        swapLevel(world, view, world.getPlayer());
        //DebugViewer dv = new DebugViewer(world, 900, 900);  //debug viewer back, fixed finally
        //dv.setLocation(820, 0);
        world.start();
    }


    private void startLevel4(int carryLives) {
        Level4 world = new Level4(carryLives, soundPlayer);
        world.setLevelCompleteListener(this);
        Level4View view = new Level4View(
                world, 800, 800,
                this::showMainMenu,
                totalScore,
                soundPlayer,
                this::startLevel1,  //start at level 1 , but also have strt level 1 buton below
                () -> startLevel2(5),  //start each level w 5 lives
                () -> startLevel3(5), //lambda, () means no params passed
                () -> startLevel4(5)//-> means run startLevel4 w 5 lives passsed in
        );


        swapLevel(world, view, world.getPlayer());

        //DEBUGGER WINDOW

        //DebugViewer dv = new DebugViewer(world, 900, 900);  //debug viewer back, fixed finally
        //dv.setLocation(820, 0);



        world.start();
    }



    //swapping level methods - removes everything from current world so can focus on new world
    private void swapLevel(World world, JPanel view, Player player) {
        if (currentWorld != null) currentWorld.stop();
        if (currentView != null) frame.remove(currentView);
        if (controller != null) {

            frame.removeKeyListener(controller);
            frame.removeMouseListener(controller.mouseAdapter);
            if (currentView != null) currentView.removeKeyListener(controller);
            if (currentView != null) currentView.removeMouseListener(controller.mouseAdapter);
        }

        currentWorld = world;
        currentView  = view;
        frame.add(view);
        frame.pack();

        //create controller + attach to frame and the view panel
        //view grabs focus after pack()
        //so register on both to guarantee key events are always received
        controller = new PlayerController(player);
        frame.addKeyListener(controller);
        view.addKeyListener(controller);
        frame.addMouseListener(controller.mouseAdapter);
        view.addMouseListener(controller.mouseAdapter);

        //make the view focusable and give it keyboard focus immediately otherwise had to press on it
        view.setFocusable(true);
        view.requestFocusInWindow();

        //step listener for continuous movement - from milestone 1
        world.addStepListener(new StepListener() {
            @Override public void preStep(StepEvent e)  {
                controller.applyMovement();
            }
            @Override public void postStep(StepEvent e) {}
        });

        frame.revalidate();
        frame.repaint();
    }

    //levelCompleteListener - if level = complete, call startlevel 2 with lives passed in etc

    @Override
    public void onLevelComplete() {
        if (currentWorld instanceof Level1) {  //if world is levl 1 , get lives+score, pass into level 2 and call level 2 after delay
            int lives = ((Level1) currentWorld).getPlayer().getLives();
            totalScore += ((Level1) currentWorld).getPlayer().getScore(); // add score
            Timer t = new Timer(2000, e -> startLevel2(lives));
            t.setRepeats(false); t.start();

        } else if (currentWorld instanceof Level2) {  //same as above but 2->3
            int lives = ((Level2) currentWorld).getPlayer().getLives();
            totalScore += ((Level2) currentWorld).getPlayer().getScore(); // add score
            Timer t = new Timer(2000, e -> startLevel3(lives));
            t.setRepeats(false); t.start();

        } else if (currentWorld instanceof Level3) {  //output in term when finished error checjing
            totalScore += ((Level3) currentWorld).getPlayer().getScore(); // add score
            System.out.println("All levels complete! Total score: " + totalScore);  //debuggin stuff
        }
    }

    //call main game to start.

    public static void main(String[] args) {
        new Game();
    }
}