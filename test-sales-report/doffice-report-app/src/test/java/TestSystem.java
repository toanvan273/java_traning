
public class TestSystem {
	public static void main(String[] args) {
		float a = 16000;
		float sum = a;
		for(int i = 0; i < 5; i++) {
			sum += sum*0.2;
			System.out.println(sum);
		}
		System.out.println(sum);
		
	}
}
