import java.awt.Color;
import java.applet.*;
import java.awt.*;
import java.util.Random;

public class ShrinkBall extends Ball
{
	protected int ShrinkRate;
	public int originalRadius;
    public int ballResetSize;

    protected int center_x;
    protected int center_y;
    
	public ShrinkBall(int radius, int initXpos, int initYpos, int speedX, int speedY, int maxBallSpeed, Color color,
			Player player, GameWindow gameW, int shrinkRate) {
		super(radius, initXpos, initYpos, speedX, speedY, maxBallSpeed, color, player, gameW);
		ShrinkRate = shrinkRate;
		originalRadius = radius;
        ballResetSize = (int) (originalRadius * 0.3);
        center_x = (gameW.x_rightout / 2) - radius;
        center_y = (gameW.y_downout / 2) - radius;
	}
	
	public boolean userHit (int maus_x, int maus_y)
    {
        double x = maus_x - pos_x;
        double y = maus_y - pos_y;

        double distance = Math.sqrt ((x*x) + (y*y));

        // random number generator
        Random rand = new Random();

        // convert shrink ratio from config to decimal value to shrink by
        float ratio = (float) (ShrinkRate/100.00);

        // check if the user hit the ball
        if (Double.compare(distance-this.radius , player.scoreConstant + Math.abs(x_speed)) <= 0)  {
            //player.addScore (((player.scoreConstant * Math.abs(x_speed) + player.scoreConstant) * 2));




            if (this.radius > ballResetSize) {
                // shrink the ball by the shrink ratio
                this.radius = (int) (this.radius * ratio);

                // score doubles each time the ball is hit
                player.addScore ((int) Math.pow((player.scoreConstant * Math.abs(x_speed) + player.scoreConstant), 2.0));
            } else {
                // set ball to original size and position the ball in the center of the applet
                this.radius = originalRadius;
                this.pos_x = center_x;
                this.pos_y = center_y;
            }

            // when the ball is hit, change the speed in both x and y randomly
            this.x_speed = rand.nextInt(this.maxspeed + 1 + this.maxspeed) - this.maxspeed;
            this.y_speed = rand.nextInt(this.maxspeed + 1 + this.maxspeed) - this.maxspeed;

            if (player.getScore() >= player.score2EarnLife) {
                player.numLives += 1;
                player.score2EarnLife += player.score2EarnLife;
            }


            return true;
        }
        else return false;
    }
	
}
