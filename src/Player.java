public class Player
{
	
	private int score;			   //player score
	public int numLives = 0;
	public int score2EarnLife = 0;
	private boolean gameover=false;	
	public int scoreConstant = 10; //This constant value is used in score calculation. You don't need to change this. 		

	public int mouseClicks = 0;
	public int successfulClicks = 0;
	public Ball mostClicked = null;
	
	public Player()
	{
		score = 0; //initialize the score to 0

	}
	
	public Player(int numLives, int score2EarnLife) {
		score = 0;
		this.numLives = numLives;
		this.score2EarnLife = score2EarnLife;
	}
	
	public int getLives()
	{
		return numLives;
	}
	
	/* get player score*/
	public int getScore ()
	{
		return score;
	}

	/*check if the game is over*/
	public boolean isGameOver ()
	{
		return gameover;
	}

	/*update player score*/
	public void addScore (int plus)
	{
		score += plus;
	}

	/*update "game over" status*/
	public void gameIsOver ()
	{
		gameover = true;
	}
}