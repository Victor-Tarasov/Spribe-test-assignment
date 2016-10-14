package victor.tarasov.oponent;

/**
 * @author Victor Tarasov
 */
public interface OpponentSearcher {
	/**
	 * Chooses opponent with minimal rating difference from current user and delete him from waiting set.
	 * Current user can't be opponent for himself.
	 * If current user contains in waiting set and there is opponent for him than both would be removed.
	 *
	 * @return eturns player with the nearest rating, or null in case when there is no waiting users or only current user is waiting for opponent.
	 */
	Player findOpponent(Player player);

	/**
	 * Waiting set can contains only one reference of each user.
	 * Adding the same user several time wouldn't affect waiting set.
	 *
	 * @return weather or not user was added to waiting set
	 */
	boolean add(Player player);
}
