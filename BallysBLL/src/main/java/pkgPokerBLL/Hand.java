package pkgPokerBLL;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import pkgPokerEnum.eCardNo;
import pkgPokerEnum.eHandStrength;
import pkgPokerEnum.eRank;
import pkgPokerEnum.eSuit;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import pkgPokerEnum.eCardNo;
import pkgPokerEnum.eHandStrength;

public class Hand {

	private UUID HandID;
	private boolean bIsScored;
	private HandScore HS;
	private ArrayList<Card> CardsInHand = new ArrayList<Card>();

	public Hand() {

	}

	public void AddCardToHand(Card c) {
		CardsInHand.add(c);
	}

	public ArrayList<Card> getCardsInHand() {
		return CardsInHand;
	}

	public HandScore getHandScore() {
		return HS;
	}

	public Hand EvaluateHand() {
		Hand h = Hand.EvaluateHand(this);
		return h;
	}

	private static Hand EvaluateHand(Hand h) {

		Collections.sort(h.getCardsInHand());

		// Another way to sort
		// Collections.sort(h.getCardsInHand(), Card.CardRank);

		HandScore hs = new HandScore();
		try {
			Class<?> c = Class.forName("pkgPokerBLL.Hand");

			for (eHandStrength hstr : eHandStrength.values()) {
				Class[] cArg = new Class[2];
				cArg[0] = pkgPokerBLL.Hand.class;
				cArg[1] = pkgPokerBLL.HandScore.class;

				Method meth = c.getMethod(hstr.getEvalMethod(), cArg);
				Object o = meth.invoke(null, new Object[] { h, hs });

				// If o = true, that means the hand evaluated- skip the rest of
				// the evaluations
				if ((Boolean) o) {
					break;
				}
			}

			h.bIsScored = true;
			h.HS = hs;

		} catch (ClassNotFoundException x) {
			x.printStackTrace();
		} catch (IllegalAccessException x) {
			x.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return h;
	}


	public static boolean isFlush(ArrayList<Card> cards) {
		boolean isFlush = false;
		int iCardCount = 0;

		for (int i = 0; i < 4; i++) {
			if (cards.get(i).geteSuit() == cards.get(i + 1).geteSuit())
				iCardCount++;
		}

		if (iCardCount == 4)
			isFlush = true;

		return isFlush;
	}


	public static boolean isStraight(ArrayList<Card> c) {
		boolean isStraight = false;
		int i = 0;
		if (Hand.isAce(c) == true) {
			i = 1;
		} else {
			i = 0;
		}
		for (; i < 4; i++) {
			if (c.get(i).geteRank().getiRankNbr() == c.get(i + 1).geteRank().getiRankNbr() + 1) {
				isStraight = true;
			} else {
				isStraight = false;
				break;
			}
		}
		return isStraight;
	}


	public static boolean isAce(ArrayList<Card> c) {
		if (c.get(0).geteRank() == eRank.ACE)
			return true;
		else
			return false;
	}


	public static boolean isHandRoyalFlush(Hand h, HandScore hs) {
		boolean isHandRoyalFlush = false;

		if (isStraight(h.CardsInHand) && isFlush(h.CardsInHand) && (h.getCardsInHand().get(0).geteRank() == eRank.ACE)
				&& (h.getCardsInHand().get(4).geteRank() == eRank.TEN)) {

			hs.setHandStrength(eHandStrength.RoyalFlush);
			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());

			isHandRoyalFlush = true;
		}
		return isHandRoyalFlush;
	}


	public static boolean isHandStraightFlush(Hand h, HandScore hs) {

		boolean isStraightFlush = false;
		if (isStraight(h.CardsInHand) && isFlush(h.CardsInHand)) {

			hs.setHandStrength(eHandStrength.StraightFlush);
			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());

			isStraightFlush = true;
		}

		return isStraightFlush;
	}


	public static boolean isHandFourOfAKind(Hand h, HandScore hs) {
		boolean isFourOfAKind = false;
		ArrayList<Card> kickers = new ArrayList<Card>();

		if (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.FourthCard.getCardNo()).geteRank()) {
			isFourOfAKind = true;

			hs.setHandStrength(eHandStrength.FourOfAKind);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));

			if (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank().getiRankNbr() > h.getCardsInHand()
					.get(eCardNo.FifthCard.getCardNo()).geteRank().getiRankNbr()) {

				hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
				hs.setLoHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());

			} else {
				hs.setHiHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());
				hs.setLoHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			}

		} else if (h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.FifthCard.getCardNo()).geteRank()) {
			isFourOfAKind = true;

			hs.setHandStrength(eHandStrength.FourOfAKind);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));

			if (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank().getiRankNbr() < h.getCardsInHand()
					.get(eCardNo.FifthCard.getCardNo()).geteRank().getiRankNbr()) {

				hs.setHiHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());
				hs.setLoHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());

			} else {
				hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
				hs.setLoHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());
			}
		}
		return isFourOfAKind;
	}


	public static boolean isHandFlush(Hand h, HandScore hs) {
		ArrayList<Card> kickers = new ArrayList<Card>();
		if (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteSuit() == h.getCardsInHand()
				.get(eCardNo.SecondCard.getCardNo()).geteSuit()
				&& (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteSuit() == h.getCardsInHand()
						.get(eCardNo.ThirdCard.getCardNo()).geteSuit())
				&& (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteSuit() == h.getCardsInHand()
						.get(eCardNo.FourthCard.getCardNo()).geteSuit())
				&& (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteSuit() == h.getCardsInHand()
						.get(eCardNo.FifthCard.getCardNo()).geteSuit())) {
			hs.setHandStrength(eHandStrength.Flush);
			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			return true;
		}

		else
			return false;
	}


	public static boolean isHandStraight(Hand h, HandScore hs) {

		ArrayList<Card> kickers = new ArrayList<Card>();

		if ((h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank().getiRankNbr() != 14)
				&& (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank()
						.getiRankNbr() == h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank()
								.getiRankNbr() - 1)
				&& (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteSuit() != h.getCardsInHand()
						.get(eCardNo.SecondCard.getCardNo()).geteSuit())
				&& (h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank()
						.getiRankNbr() == h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank().getiRankNbr()
								- 1)
				&& (h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteSuit() != h.getCardsInHand()
						.get(eCardNo.ThirdCard.getCardNo()).geteSuit())
				&& (h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank()
						.getiRankNbr() == h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank()
								.getiRankNbr() - 1)
				&& (h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteSuit() != h.getCardsInHand()
						.get(eCardNo.FourthCard.getCardNo()).geteSuit())
				&& (h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank()
						.getiRankNbr() == h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank().getiRankNbr()
								- 1)
				&& (h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteSuit() != h.getCardsInHand()
						.get(eCardNo.FifthCard.getCardNo()).geteSuit())) {

			hs.setHandStrength(eHandStrength.Straight);
			hs.setHiHand(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setKickers(kickers);
			return true;
		}

		return false;
	}


	public static boolean isHandThreeOfAKind(Hand h, HandScore hs) {
		boolean isThreeOfAKind = false;

		ArrayList<Card> kickers = new ArrayList<Card>();

		if (h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.ThirdCard.getCardNo()).geteRank()) {

			isThreeOfAKind = true;
			hs.setHandStrength(eHandStrength.ThreeOfAKind);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));

		} else if (h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.FourthCard.getCardNo()).geteRank()) {

			isThreeOfAKind = true;
			hs.setHandStrength(eHandStrength.ThreeOfAKind);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));

		} else if (h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.FifthCard.getCardNo()).geteRank()) {

			isThreeOfAKind = true;
			hs.setHandStrength(eHandStrength.ThreeOfAKind);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()));

		}

		return isThreeOfAKind;
	}


	public static boolean isHandTwoPair(Hand h, HandScore hs) {
		boolean isHandTwoPair = false;

		ArrayList<Card> kickers = new ArrayList<Card>();

		if ((h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.SecondCard.getCardNo()).geteRank())
				&& (h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank() == h.getCardsInHand()
						.get(eCardNo.FourthCard.getCardNo()).geteRank())) {
			isHandTwoPair = true;
			hs.setHandStrength(eHandStrength.TwoPair);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank());
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));

		} else if ((h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.ThirdCard.getCardNo()).geteRank())
				&& (h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank() == h.getCardsInHand()
						.get(eCardNo.FifthCard.getCardNo()).geteRank())) {
			isHandTwoPair = true;
			hs.setHandStrength(eHandStrength.TwoPair);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank());
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));

		}

		return isHandTwoPair;
	}


	public static boolean isHandPair(Hand h, HandScore hs) {

		boolean isHandPair = false;

		ArrayList<Card> kickers = new ArrayList<Card>();

		if ((h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.SecondCard.getCardNo()).geteRank())) {
			isHandPair = true;
			hs.setHandStrength(eHandStrength.Pair);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));

		} else if ((h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.ThirdCard.getCardNo()).geteRank())) {
			isHandPair = true;
			hs.setHandStrength(eHandStrength.Pair);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));

		} else if ((h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.FourthCard.getCardNo()).geteRank())) {
			isHandPair = true;
			hs.setHandStrength(eHandStrength.Pair);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));
		} else if ((h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.FifthCard.getCardNo()).geteRank())) {
			isHandPair = true;
			hs.setHandStrength(eHandStrength.Pair);

			hs.setHiHand(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank());
			hs.setLoHand(null);
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()));
		}
		return isHandPair;
	}


	public static boolean isHandHighCard(Hand h, HandScore hs) {
		boolean isHandHighCard = false;

		ArrayList<Card> kickers = new ArrayList<Card>();

		if ((h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() != h.getCardsInHand()
				.get(eCardNo.FifthCard.getCardNo()).geteRank())) {
			isHandHighCard = true;
			hs.setHandStrength(eHandStrength.HighCard);

			hs.setHiHand(null);
			hs.setLoHand(null);

			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.SecondCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()));
			hs.getKickers().add(h.getCardsInHand().get(eCardNo.FifthCard.getCardNo()));
		}
		return isHandHighCard;
	}


	public static boolean isAcesAndEights(Hand h, HandScore hs) {
		return false;
	}

	public static boolean isHandFullHouse(Hand h, HandScore hs) {

		boolean isFullHouse = false;

		ArrayList<Card> kickers = new ArrayList<Card>();
		if ((h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.ThirdCard.getCardNo()).geteRank())
				&& (h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank() == h.getCardsInHand()
						.get(eCardNo.FifthCard.getCardNo()).geteRank())) {
			isFullHouse = true;
			hs.setHandStrength(eHandStrength.FullHouse);
			hs.setHiHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.FourthCard.getCardNo()).geteRank());
		} else if ((h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank() == h.getCardsInHand()
				.get(eCardNo.SecondCard.getCardNo()).geteRank())
				&& (h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank() == h.getCardsInHand()
						.get(eCardNo.FifthCard.getCardNo()).geteRank())) {
			isFullHouse = true;
			hs.setHandStrength(eHandStrength.FullHouse);
			hs.setHiHand(h.getCardsInHand().get(eCardNo.ThirdCard.getCardNo()).geteRank());
			hs.setLoHand(h.getCardsInHand().get(eCardNo.FirstCard.getCardNo()).geteRank());
		}

		return isFullHouse;

	}
}
