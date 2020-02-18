
In Eclipse, right-click on the src/main/java folder in the package explorer and select New Class.  Name the new class "CatPeople" to match our main class from the pom.xml file.  This class serves as the entry point when our application runs.  It's very simple - it only needs to set up the Aviator Core Framework.

We're going to start with the "test" consensus mechanism, which simulates how your application will run when it's connected to a real network without having to go through the effort to set one up.

```java
import com.txmq.aviator.core.Aviator;
import com.txmq.aviator.core.AviatorTestConsensus;

public class CatPeople {

	/**
	 * All we need to do here is configure the core framework
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Aviator.init(new AviatorTestConsensus());
		} catch (ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
```

In our main program, we simply call Aviator.init(), and pass it an instance of AviatorTestConsensus.