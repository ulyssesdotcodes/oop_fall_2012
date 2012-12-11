

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


// This is what a chained method call looks like in the Java AST.
/*
    CallExpression(
      CallExpression(
        CallExpression(
          PrimaryIdentifier(
            "mc"
          ),
          null,
          "c",
          Arguments()
        ),
        null,
        "c",
        Arguments()
      ),
      null,
      "c",
      Arguments()
    )
*/

// What I'd like it to look like in the C++ AST.
// TODO: Can I handle nested method chains inside of other method chains?
/*


	C++ code:

	({
		::MethodChain __anonymous1 = mc->__vptr->c(mc);
		::MethodChain __anonymous2 = __anonymous1->__vptr->c(__anonymous1);
		__anonymous2->__vptr->c(__anonymous2);
	});

	AST:

	BracedGroupExpression(
		BracedGroupLink(
			Type(
				"::MethodChain",
				null
			),
			Declarator(
				"__anonymous1",
				CallExpression(
					"mc",
					"c",
					Arguments("mc")
				)
			)
		),
		BracedGroupLink(
			Type(
				"::MethodChain",
				null
			),
			Declarator(
				"__anonymous2",
				CallExpression(
					"__anonymous1",
					"c",
					Arguments("__anonymous1")
				)
			)
		),
		BracedGroupReturn(
			CallExpression(
				"__anonymous2",
				"c",
				Arguments("__anonymous2")
			)			
		)
	)

	// Can I generalize the braced group expression?
/*

	C++ code:
	// mc.c()

	AST:

	BracedGroupExpression(
		BracedGroupReturn(
			CallExpression(
				"mc",
				"c",
				Arguments("mc")
			)
		)
	)


*/

// Is there going to be a problem with incrementing?
// No, don't change ExpressionStatement. Only change CallExpression.
