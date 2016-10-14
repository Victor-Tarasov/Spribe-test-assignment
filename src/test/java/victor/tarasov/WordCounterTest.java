package victor.tarasov;

import org.junit.Before;
import org.junit.Test;
import victor.tarasov.counter.SynchronizedWordCounter;
import victor.tarasov.counter.WordCounter;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * @author Victor Tarasov
 */
public class WordCounterTest {
	private WordCounter counter;

	@Before
	public void init() {
		counter = new SynchronizedWordCounter();
	}

	@Test
	public void countWord() {
		String firstWord = "someWord";
		int wordCount = new Random().nextInt(10);
		fill(firstWord, wordCount);
		assertEquals(wordCount, counter.getCount(firstWord));
	}

	@Test
	public void caseIgnoreTest() {
		String word = "someWord";
		counter.addAll("someword");
		counter.addAll("SOMEWORD");
		counter.addAll("SoMeWoRd");
		assertEquals(3, counter.getCount(word));
	}

	@Test
	public void splitTest() {
		String text = "firs second can't couldn`t  back-end:front-end \t third \n fifth*sixth 123";
		String[] words = {"firs",  "second", "can't", "couldn`t",  "back-end", "front-end", "third", "fifth", "sixth", "123"};
		counter.addAll(text);
		Arrays.stream(words).forEach(word -> assertEquals(1, counter.getCount(word)));
	}

	@Test
	public void twoTexts() {
		String firstWord = "first";
		String secondWord = "second";
		String thirdWord = "third";
		String text = String.join(" ", firstWord, secondWord);
		counter.addAll(text, String.join(" ", text, thirdWord));
		assertEquals(2, counter.getCount(firstWord));
		assertEquals(2, counter.getCount(secondWord));
		assertEquals(1, counter.getCount(thirdWord));
	}

	@Test
	public void concurrentSplitTest() throws InterruptedException {
		int count = 100000;
		String word = "someWord";
		String text = Stream.generate(() -> word).limit(count).collect(Collectors.joining(" "));
		new Thread(() -> counter.addAll(text)).start();
		Thread.sleep(10);		// prevent call to count before call to addAll
		assertEquals(count, counter.getCount(word));
	}

	@Test
	public void concurrentAddTest() throws InterruptedException {
		int count = 10000;
        String word = "someWord";
        String[] words = Stream.generate(() -> "someWord").limit(count).toArray(String[]::new);
        new Thread(() -> counter.addAll(words)).start();
		Thread.sleep(10);		// prevent call to count before call to addAll
		assertEquals(count, counter.getCount(word));
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToCountText() {
		counter.getCount("Some text");
	}

	private void fill(String word, int count) {
		counter.addAll(Stream.generate(() -> word).limit(count).toArray(String[]::new));
	}
}
