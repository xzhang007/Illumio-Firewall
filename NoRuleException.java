/**
 * This class is an defined Exception class for some case like the input rule file is not an complete file,
 * it does not contain all the 4 combination of direction and protocol
 */
public class NoRuleException extends Exception {

	public NoRuleException() {
		super("No Such Direction And Protocol In Rules");
	}
}
