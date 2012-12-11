

public class MethodChain {
	static int count = 0;

	public MethodChain c() {
		count++;
		return this;
	}

	public static void main(String[] args) {
		MethodChain mc = new MethodChain();
		mc.c().c().c();
    System.out.println(MethodChain.count);
	}
}

