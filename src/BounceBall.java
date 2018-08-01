import java.awt.Color;
import java.applet.*;
import java.awt.*;
import java.util.Random;
public class BounceBall extends Ball
{
	public int maxNumBounces;
    public int currentBounces;
	protected int BounceCount;
	public BounceBall(int radius, int initXpos, int initYpos, int speedX, int speedY, int maxBallSpeed, Color color,
			Player player, GameWindow gameW, int bounceCount) {
		super(radius, initXpos, initYpos, speedX, speedY, maxBallSpeed, color, player, gameW);
		BounceCount = bounceCount;
		maxNumBounces = 0;
        currentBounces = 0;
	}
	protected boolean isOut ()
    {
        Random rand = new Random();
        if ((pos_x < gameW.x_leftout) || (pos_x > gameW.x_rightout) || (pos_y < gameW.y_upout) || (pos_y > gameW.y_downout)) {

            // if the current bounces is less than or equal to max bounces, change the direction or "bounce" the ball
            if (currentBounces <= maxNumBounces) {
                this.x_speed = Math.negateExact(this.x_speed);
                this.y_speed = Math.negateExact(this.y_speed);
                return false;
            } else {
                // if the player has no lives left, end the game
                if (player.numLives == 0) {
                    player.gameIsOver();
                    return true;
                } else {
                    // subtract a life from the player
                    player.numLives -= 1;

                    // ball appears at initial starting location and has random speed
                    this.x_speed = rand.nextInt(this.maxspeed + 1 + Math.negateExact(this.maxspeed)) - this.maxspeed;
                    this.y_speed = rand.nextInt(this.maxspeed + 1 + Math.negateExact(this.maxspeed)) - this.maxspeed;

                    return true;
                }
            }

        }

        return false;
    }


}
