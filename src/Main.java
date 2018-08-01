import java.awt.*;
import java.util.*;
import java.applet.*;
import java.awt.event.MouseEvent;
import javax.swing.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*<applet code="Main" height=400 width=400></applet>*/

public class Main extends Applet implements Runnable
{
/* Configuration arguments. These should be initialized with the values read from the config.JSON file*/					
	private int numBalls;
	private int x_leftout;
	private int x_rightout;
	private int y_upout;
	private int y_downout;
	private int numLives;
	private int score2EarnLife;
/*end of config arguments*/

    private int refreshrate = 15;	           //Refresh rate for the applet screen. Do not change this value. 
	private boolean isStoped = true;		     
    Font f = new Font ("Arial", Font.BOLD, 18);
	
	private Player player;			           //Player instance.
	//private Ball balls;                      //Ball instance. You need to replace this with an array of balls.  
	public ArrayList<Ball> balls = new ArrayList<Ball>();
	Thread th;						           //The applet thread. 
	  
    Cursor c;				
    private GameWindow gwindow;                 // Defines the borders of the applet screen. A ball is considered "out" when it moves out of these borders.
	private Image dbImage;
	private Graphics dbg;

	
	class HandleMouse extends MouseInputAdapter 
	{

    	public HandleMouse() 
    	{
            addMouseListener(this);
        }
		
    	public void mouseClicked(MouseEvent e) 
    	{
    		// add 1 to the mouse clicks for the current player
    		player.mouseClicks++;
    		
    		//MAKE A LOOP
        	if (isStoped == true) {
        		for(int i = 0; i < balls.size(); i++)
        		{
					if (balls.get(i).userHit (e.getX(), e.getY())) {
						balls.get(i).ballWasHit ();
						// set the ball as clicked
						balls.get(i).BallClicked();
						// add 1 to the successful clicks for the user
						player.successfulClicks++;
		        	}
        		}
        		
			}
			else if (e.getClickCount() == 2){
				isStoped = false;
				init ();
			}
    		
    	}

    	public void mouseReleased(MouseEvent e) 
    	{
           
    	}
        
    	public void RegisterHandler() 
    	{

    	}
    }
	
	HandleMouse hm = new HandleMouse();
	
	//JSON reader; you need to complete this function
	public void JSONReader()
	{
		try
		{
			//read the json file
			FileReader reader = new FileReader("C:\\Users\\hasnain\\Documents\\Spring2017\\Cpts355\\HW6_BallGame\\src\\config.JSON");
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParser.parse(reader);
			
			// handle a structure into the json object for GameWindow
			JSONObject GameWindowStruct = (JSONObject) jsonObject.get("GameWindow");
			x_leftout = Integer.parseInt((String)GameWindowStruct.get("x_leftout"));
			x_rightout = Integer.parseInt((String)GameWindowStruct.get("x_rightout"));
			y_upout = Integer.parseInt((String)GameWindowStruct.get("y_upout"));
			y_downout = Integer.parseInt((String)GameWindowStruct.get("y_downout"));
			
			// create new game window object
			GameWindow newWindow = new GameWindow(x_leftout, x_rightout, y_upout, y_downout);

			// set the game window to the new window
			this.gwindow = newWindow;
			
			// handle a structure into the json object for Player
			JSONObject PlayerStruct = (JSONObject) jsonObject.get("Player");
			numLives = Integer.parseInt((String)PlayerStruct.get("numLives"));
			score2EarnLife = Integer.parseInt((String)PlayerStruct.get("score2EarnLife"));
			player = new Player (numLives, score2EarnLife);

			
			// get an int from the JSON object
			numBalls = Integer.parseInt((String) jsonObject.get("numBalls"));

			// get an array from the JSON object
			JSONArray ball = (JSONArray) jsonObject.get("Ball");
			
			Iterator i = ball.iterator();
			// take each value from the json array separately
			while (i.hasNext()) 
			{
				JSONObject innerObj = (JSONObject) i.next();
				String type = (String)innerObj.get("type");
				if(type.equals("basicball"))
				{
					balls.add(new Ball(
							Integer.parseInt((String)innerObj.get("radius")),
							Integer.parseInt((String)innerObj.get("initXpos")),
							Integer.parseInt((String)innerObj.get("initYpos")),
							Integer.parseInt((String)innerObj.get("speedX")),
							Integer.parseInt((String)innerObj.get("speedY")),
							Integer.parseInt((String)innerObj.get("maxBallSpeed")),
							Color.red, player, gwindow
							));
				}
				else if(type.equals("bounceball"))
				{
					balls.add(new BounceBall(
							Integer.parseInt((String)innerObj.get("radius")),
							Integer.parseInt((String)innerObj.get("initXpos")),
							Integer.parseInt((String)innerObj.get("initYpos")),
							Integer.parseInt((String)innerObj.get("speedX")),
							Integer.parseInt((String)innerObj.get("speedY")),
							Integer.parseInt((String)innerObj.get("maxBallSpeed")),
							Color.blue, player, gwindow,
							Integer.parseInt((String)innerObj.get("bounceCount"))
							));
				}
				else
				{
					balls.add(new ShrinkBall(
							Integer.parseInt((String)innerObj.get("radius")),
							Integer.parseInt((String)innerObj.get("initXpos")),
							Integer.parseInt((String)innerObj.get("initYpos")),
							Integer.parseInt((String)innerObj.get("speedX")),
							Integer.parseInt((String)innerObj.get("speedY")),
							Integer.parseInt((String)innerObj.get("maxBallSpeed")),
							Color.green, player, gwindow,
							Integer.parseInt((String)innerObj.get("shrinkRate"))
							));
				}
			}
			
		}
		catch (FileNotFoundException ex) {ex.printStackTrace();}
		catch (IOException ex) { ex.printStackTrace(); }
		catch (ParseException ex) { ex.printStackTrace(); }
		catch (NullPointerException ex) { ex.printStackTrace(); }
	}
	
	/*initialize the game*/
	public void init ()
	{	
		//reads info from JSON doc
		this.JSONReader();
		c = new Cursor (Cursor.CROSSHAIR_CURSOR);
		this.setCursor (c);	
				
		setBackground (Color.black);
		setFont (f);

		if (getParameter ("refreshrate") != null) {
			refreshrate = Integer.parseInt(getParameter("refreshrate"));
		}
		else refreshrate = 15;

		
		
		/* The parameters for the GameWindow constructor (x_leftout, x_rightout, y_upout, y_downout) 
		should be initialized with the values read from the config.JSON file*/	
		//gwindow = new GameWindow(x_leftout,x_rightout,y_upout,y_downout);
		this.setSize(gwindow.x_rightout, gwindow.y_downout); //set the size of the applet window.
		
		/*The skeleton code creates a single basic ball. Your game should support arbitrary number of balls. 
		* The number of balls and the types of those balls are specified in the config.JSON file.
		* The ball instances will be stores in an Array or Arraylist.  */
		/* The parameters for the Ball constructor (radius, initXpos, initYpos, speedX, speedY, maxBallSpeed, color) 
		should be initialized with the values read from the config.JSON file. Note that the "color" need to be initialized using the RGB values provided in the config.JSON file*/
		//balls = new Ball(15, 400, 500, 1, -1, 4, Color.red, player, gwindow);
		//numBalls = 1;
		
	}
	
	/*start the applet thread and start animating*/
	public void start ()
	{		
		if (th==null){
			th = new Thread (this);
		}
		th.start ();
	}
	
	/*stop the thread*/
	public void stop ()
	{
		th=null;
	}

    
	public void run ()
	{	
		/*Lower this thread's priority so it won't interfere with other processing going on*/
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        /*This is the animation loop. It continues until the user stops or closes the applet*/
		while (true) {
			if (isStoped == true) {
				// move each ball in the array of balls accordingly
				for(int i = 0; i < numBalls; i++) {
					this.balls.get(i).move();
				}
			}
            /*Display it*/
			repaint();
            
			try {
				
				Thread.sleep (refreshrate);
			}
			catch (InterruptedException ex) {
				
			}			
		}
	}

	
	public void paint (Graphics g)
	{
		/*if the game is still active draw the ball and display the player's score. If the game is active but stopped, ask player to double click to start the game*/ 
		if (player.isGameOver() == false) {
			g.setColor (Color.yellow);
			
			g.drawString ("Score: " + player.getScore(), 10, 40);
			g.drawString("Lives:" + player.getLives(), 10, 70); // The player lives need to be displayed
			
			// draw each ball in the ball array
			for(int i = 0; i < numBalls; i++) {
				Ball temp = this.balls.get(i);
				temp.DrawBall(g);
			}
			
			if (isStoped == false) {
				g.setColor (Color.yellow);
				g.drawString ("Doubleclick on Applet to start Game!", 40, 200);
			}
		}
		/*if the game is over (i.e., the ball is out) display player's score*/
		else {
			g.setColor (Color.yellow);
			
			Ball mostClicked = balls.get(0);

			// calculate the ball that was most clicked
			for(int i = 1; i < balls.size(); i++) {
				if (mostClicked.numClicked < balls.get(i).numClicked) {
					mostClicked = balls.get(i);
				}
			}

			// set the players most clicked ball
			player.mostClicked = mostClicked;

			String ballType = null;

			if(mostClicked instanceof ShrinkBall)
				ballType = "Shrink Ball";
			else if (mostClicked instanceof BounceBall)
				ballType = "Bounce Ball";
			else 
				ballType = "basic Ball";
			
			g.drawString ("Game over!", 130, 100);
			g.drawString ("You scored " + player.getScore() + " Points!", 90, 140);
			
			g.drawString("Statistics: ", 400, 160);
			g.drawString("Number of Clicks: " + player.mouseClicks, 400, 180); // The number of clicks need to be displayed
			float percentage = (float)((player.successfulClicks* 100.00)/player.mouseClicks);
			g.drawString("% of Successful Clicks: " + percentage + "% ", 400, 200); // The % of successful clicks need to be displayed
			g.drawString("Ball most hit: " + ballType, 400, 240); // The nball that was hit the most need to be displayed
				
			g.drawString ("Doubleclick on the Applet, to play again!", 20, 220);

			isStoped = true;	
		}
	}

	
	public void update (Graphics g)
	{
		
		if (dbImage == null)
		{
			dbImage = createImage (this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics ();
		}

		
		dbg.setColor (getBackground ());
		dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);

		
		dbg.setColor (getForeground());
		paint (dbg);

		
		g.drawImage (dbImage, 0, 0, this);
	}
}


