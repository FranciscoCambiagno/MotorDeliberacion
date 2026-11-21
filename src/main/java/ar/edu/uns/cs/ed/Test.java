package ar.edu.uns.cs.ed;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		af(" as ");
	}
	
	private static void af(String a) {
		System.out.println(a == " as ");
		a = a.trim();
		System.out.println("-"+a+"-");
		System.out.println(a.equals("as"));
		System.out.println(a == "as");
	}

}
