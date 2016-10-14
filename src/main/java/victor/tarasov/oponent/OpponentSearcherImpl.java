package victor.tarasov.oponent;

import com.google.common.collect.TreeMultimap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;


/**
 * @author Victor Tarasov
 */
public class OpponentSearcherImpl implements OpponentSearcher {
	private final TreeMultimap<Double, Player> players = TreeMultimap.create(Double::compare, Comparator.comparingInt(Object::hashCode));

	@Override
	public Player findOpponent(final Player player) {
		if (players.isEmpty() || players.containsValue(player) && players.size() == 1) return null;

	// process case if player is in the waiting set
		players.remove(player.getRating(), player);

	// get the nearest entries
		double rating = player.getRating();
		NavigableMap<Double, Collection<Player>> ratingPlayersMap = players.asMap();
		Entry<Double, Collection<Player>> smallerEntry = ratingPlayersMap.floorEntry(rating);
		Entry<Double, Collection<Player>> higherEntry = ratingPlayersMap.higherEntry(rating);

	// process case when there is no nearest player
		if (smallerEntry == null && higherEntry == null) return null;

	// select the closest players
		Collection<Player> closestPlayers;
		if (smallerEntry != null &&
				(higherEntry == null || Math.abs(smallerEntry.getKey() - rating) < Math.abs(higherEntry.getKey() - rating))) {
			closestPlayers = smallerEntry.getValue();
		} else {
			closestPlayers = higherEntry.getValue();
		}

	// choose an opponent and remove it from waiting set
		Optional<Player> opponent = closestPlayers.stream().filter(p -> p != player).findFirst();
		if (!opponent.isPresent()) return null;
		players.remove(opponent.get().getRating(), opponent.get());
		return opponent.get();
	}

	@Override
	public boolean add(final Player player) {
		return players.put(player.getRating(), player);
	}
}
