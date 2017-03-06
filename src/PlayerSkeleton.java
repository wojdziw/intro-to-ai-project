
public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {

		return 0;
	}

	public int calculateFeature1(State s) {
		int featureValue = 0;
		return featureValue;
	}

	public int calculateFeature2(State s) {
		int featureValue = 0;
		return featureValue;
	}

	public int calculateFeature3(State s) {
		int featureValue = 0;
		return featureValue;
	}

	public int calculateUtility(State s) {
		int noFeatures = 3;

		int[] weights = new int[noFeatures];

		int feature1 = calculateFeature1(s);
		int feature2 = calculateFeature2(s);
		int feature3 = calculateFeature2(s);

		int utility = weights[0]*feature1 + weights[1]*feature2 + weights[2]*feature3;

		return utility;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
