package victor.tarasov.counter;

/**
 * @author Victor Tarasov
 */
public interface WordCounter {
	void addAll(String... texts);
	int getCount(String word);
}
