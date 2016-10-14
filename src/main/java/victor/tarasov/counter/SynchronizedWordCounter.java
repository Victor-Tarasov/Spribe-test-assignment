package victor.tarasov.counter;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import java.util.Arrays;


/**
 * @author Victor Tarasov
 */
public class SynchronizedWordCounter implements WordCounter {
	private static final String WORD_SEPARATORS = "[^\\w'`-]+";
	private final Multiset<String> words = TreeMultiset.create(String::compareToIgnoreCase);

	@Override
	synchronized public void addAll(String... texts) {
		Arrays.stream(texts).flatMap(text -> Arrays.stream(text.split(WORD_SEPARATORS))).forEach(words::add);
	}

	@Override
	synchronized public int getCount(String word) {
		if (word.split(WORD_SEPARATORS).length > 1) throw new IllegalArgumentException("Only one word can be accepted.");
		return words.count(word);
	}
}
