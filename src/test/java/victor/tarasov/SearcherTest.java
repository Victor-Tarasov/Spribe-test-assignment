package victor.tarasov;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import victor.tarasov.oponent.OpponentSearcher;
import victor.tarasov.oponent.OpponentSearcherImpl;
import victor.tarasov.oponent.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Victor Tarasov
 */
public class SearcherTest {
	private OpponentSearcher searcher;

	@Before
	public void init() {
		searcher = new OpponentSearcherImpl();
	}

	@Test
	public void getFromEmpty() {
		assertEquals(null, searcher.findOpponent(generateRandomPlayer()));
	}

	@Test
	public void getFromEmptied() {
		int userCount = 10;
		fill(searcher, userCount);
		for (int i = 0; i < userCount; i++) {
			searcher.findOpponent(generateRandomPlayer());
		}
		assertEquals(null, searcher.findOpponent(generateRandomPlayer()));
	}

	@Test
	public void onePlayerWithGreaterRating() {
		Player weak = new Player("Weak", 11.6);
		Player strong = new Player("Strong", 11);
		onlyOneOpponentTest(strong, weak);
	}

	@Test
	public void onePlayerWithSmallerRating() {
		Player weak = new Player("Weak", 11.6);
		Player strong = new Player("Strong", 11);
		onlyOneOpponentTest(weak, strong);
	}

	private void onlyOneOpponentTest(Player waitingPlayer, Player player) {
		searcher.add(waitingPlayer);
		assertTrue(searcher.findOpponent(player) == waitingPlayer);
	}

	@Test
	public void order() {
		List<Player> players = Lists.newArrayList(new Player("Player1", 7),
				new Player("Player2", 7.3),
				new Player("Player3", 6.4),
				new Player("Player4", 5));
		List<Player> shuffledPlayers = Lists.newArrayList(players);
		Collections.shuffle(shuffledPlayers);
		shuffledPlayers.forEach(searcher::add);

		Player somePlayer = new Player("SomePlayer", 7);
		for (Player player : players) {
			assertTrue(player == searcher.findOpponent(somePlayer));
		}
	}

	@Test
	public void nearestFromTheTop() {
		int searchedRating = 2;
		Player topPlayer = new Player("TopPlayer", searchedRating + 1);
		searcher.add(new Player("BottomPlayer", searchedRating - 1.1));
		searcher.add(topPlayer);
		assertTrue(searcher.findOpponent(new Player("SomePlayer", searchedRating)) == topPlayer);
	}

	@Test
	public void nearestFromTheBottom() {
		int searchedRating = new Random().nextInt();
		Player bottomPlayer = new Player("BottomPlayer", searchedRating + 1);
		searcher.add(new Player("TopPlayer", searchedRating - 1.1));
		searcher.add(bottomPlayer);
		assertTrue(searcher.findOpponent(new Player("SomePlayer", searchedRating)) == bottomPlayer);
	}

	@Test
	public void containsAll() {
		assertSearcherPlayers(fill(searcher, 10));
	}

	@Test
	public void playersWithSameRating() {
		double rating = new Random().nextDouble();
		List<Player> players = Lists.newArrayList(new Player("first", rating), new Player("second", rating));
		players.forEach(searcher::add);
		assertSearcherPlayers(players);
	}

	@Test
	public void playersWithSameRatingAndSamePlayer() {
		double rating = new Random().nextDouble();
		Player player = new Player("first", rating);
		Player otherPlayer = new Player("second", rating);
		searcher.add(player);
		searcher.add(otherPlayer);
		assertTrue(otherPlayer == searcher.findOpponent(player));
		assertEquals(null, searcher.findOpponent(generateRandomPlayer()));
	}

	@Test
	public void insertSamePlayer() {
		Player player = generateRandomPlayer();
		searcher.add(player);
		searcher.add(player);
		assertSearcherPlayers(Lists.newArrayList(player));
	}

	@Test
	public void getOpponentFromTheSamePlayerSet() {
		Player player = generateRandomPlayer();
		searcher.add(player);
		assertEquals(null, searcher.findOpponent(player));
		assertTrue(player == searcher.findOpponent(generateRandomPlayer()));
	}

	@Test
	public void getOpponentFromTheSetWithSamePlayer() {
		Player player = generateRandomPlayer();
		Player otherPlayer = generateRandomPlayer();
		searcher.add(player);
		searcher.add(otherPlayer);
		assertTrue(otherPlayer == searcher.findOpponent(player));
		assertEquals(null, searcher.findOpponent(generateRandomPlayer()));
	}

	private void assertSearcherPlayers(List<Player> players) {
		List<Player> playersInSearcher = new ArrayList<>();
		Player player = searcher.findOpponent(generateRandomPlayer());
		while (player != null) {
			playersInSearcher.add(player);
			player = searcher.findOpponent(generateRandomPlayer());
		}
		assertTrue(players.containsAll(playersInSearcher) && playersInSearcher.size() == players.size());
	}

	private List<Player> fill(OpponentSearcher opponentSearcher, int playerCount) {
		List<Player> players = new ArrayList<>();
		for (int i = 0; i < playerCount; i++) {
			Player player = generateRandomPlayer();
			players.add(player);
			opponentSearcher.add(player);
		}
		return players;
	}

	private Player generateRandomPlayer() {
		Random random = new Random();
		byte[] bytes = new byte[random.nextInt(50)];
		random.nextBytes(bytes);
		return new Player(new String(bytes), random.nextDouble());
	}
}
