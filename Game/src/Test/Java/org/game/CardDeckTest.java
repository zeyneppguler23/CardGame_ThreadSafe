package org.game;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.CountDownLatch;

public class CardDeckTest {

    private CardDeck cardDeck;

    @BeforeEach
    void setUp() {
        cardDeck = new CardDeck();
    }

    @AfterEach
    void tearDown() {
        // Clean up the card deck after each test
        cardDeck.clear();
    }


    // Test 1: Can you add a card to the deck?
    @Test
    void testAddCard() {
        Card card = new Card(5);
        cardDeck.addCard(card);

        List<Card> cards = cardDeck.getCardsAsList();
        assertEquals(1, cards.size(), "Deck should contain 1 card after adding one card.");
        assertEquals(5, cards.get(0).getFaceValue(), "The card added should have face value 5."); // Use get(0) instead of getFirst()
    }


    // Test 2: Does a card get drawn?
    @Test
    void testDrawCard() {
        Card card = new Card(10);
        cardDeck.addCard(card);

        Card drawnCard = cardDeck.drawCard();
        assertNotNull(drawnCard, "Card should be drawn.");
        assertEquals(10, drawnCard.getFaceValue(), "The drawn card should have face value 10.");

        // After drawing the card, the deck should be empty
        assertTrue(cardDeck.isEmpty(), "Deck should be empty after drawing all cards.");
    }

    // Test 3: How does the game deal with drawing from an empty deck?
    @Test
    void testDrawCardFromEmptyDeck() {
        Card drawnCard = cardDeck.drawCard();
        assertNull(drawnCard, "Drawing from an empty deck should return null.");
    }

    // Test 4: How does a game deal with an empty deck?
    @Test
    void testIsEmpty() {
        assertTrue(cardDeck.isEmpty(), "Deck should be empty initially.");

        // After adding a card, the deck should no longer be empty
        cardDeck.addCard(new Card(7));
        assertFalse(cardDeck.isEmpty(), "Deck should not be empty after adding a card.");

        // After drawing the card, the deck should be empty again
        cardDeck.drawCard();
        assertTrue(cardDeck.isEmpty(), "Deck should be empty after drawing the last card.");
    }

    // Test 5: Do the card values get converted to strings?
    @Test
    void testToString() {
        cardDeck.addCard(new Card(5));
        cardDeck.addCard(new Card(10));

        String deckString = cardDeck.toString();
        assertTrue(deckString.contains("Deck: "), "Deck string should contain 'Deck:'");
        assertTrue(deckString.contains("5"), "Deck string should contain card 5");
        assertTrue(deckString.contains("10"), "Deck string should contain card 10");
    }

    // Test 6: Offering
    @Test
    void testOffer() {
        Card card = new Card(3);
        cardDeck.offer(card);

        // Deck should have the card after offering it
        List<Card> cards = cardDeck.getCardsAsList();
        assertEquals(1, cards.size(), "Deck should contain 1 card after offering a card.");
        assertEquals(3, cards.get(0).getFaceValue(), "The card offered should have face value 3."); // Use get(0) instead of getFirst()
    }


    // Test 7: Counting cards after each turn
    @Test
    void testAddMultipleCards() {
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);

        cardDeck.addCard(card1);
        cardDeck.addCard(card2);
        cardDeck.addCard(card3);

        List<Card> cards = cardDeck.getCardsAsList();
        assertEquals(3, cards.size(), "Deck should contain 3 cards after adding three cards.");
        assertEquals(1, cards.get(0).getFaceValue(), "First card should have face value 1.");
        assertEquals(2, cards.get(1).getFaceValue(), "Second card should have face value 2.");
        assertEquals(3, cards.get(2).getFaceValue(), "Third card should have face value 3.");
    }

    // Test 8: Testing order of deck after a turn remains consistant
    @Test
    void testOfferPreservesOrder() {
        Card card1 = new Card(1);
        Card card2 = new Card(2);

        cardDeck.offer(card1);
        cardDeck.offer(card2);

        List<Card> cards = cardDeck.getCardsAsList();
        assertEquals(2, cards.size(), "Deck should contain 2 cards after offering two cards.");
        assertEquals(1, cards.get(0).getFaceValue(), "First card should have face value 1.");
        assertEquals(2, cards.get(1).getFaceValue(), "Second card should have face value 2.");
    }

    // Test 9: Drawing tests
    @Test
    void testDrawFromDeckWithMultipleCards() {
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);

        cardDeck.addCard(card1);
        cardDeck.addCard(card2);
        cardDeck.addCard(card3);

        Card drawnCard = cardDeck.drawCard();
        assertNotNull(drawnCard, "Card should be drawn.");
        assertEquals(1, drawnCard.getFaceValue(), "First drawn card should have face value 1.");

        drawnCard = cardDeck.drawCard();
        assertNotNull(drawnCard, "Card should be drawn.");
        assertEquals(2, drawnCard.getFaceValue(), "Second drawn card should have face value 2.");

        drawnCard = cardDeck.drawCard();
        assertNotNull(drawnCard, "Card should be drawn.");
        assertEquals(3, drawnCard.getFaceValue(), "Third drawn card should have face value 3.");

        assertTrue(cardDeck.isEmpty(), "Deck should be empty after drawing all cards.");
    }

    // Test 10: Ensuring an empty deck is represented as a string
    @Test
    void testToStringWithEmptyDeck() {
        String deckString = cardDeck.toString();
        assertEquals("Deck: []", deckString, "Empty deck should have a string representation of 'Deck: []'.");
    }

    // Test 11: Testing play-through
    @Test
    void testOfferAndDraw() {
        Card card1 = new Card(1);
        Card card2 = new Card(2);

        cardDeck.offer(card1);
        cardDeck.offer(card2);

        Card drawnCard = cardDeck.drawCard();
        assertNotNull(drawnCard, "Card should be drawn.");
        assertEquals(1, drawnCard.getFaceValue(), "First drawn card should have face value 1.");

        drawnCard = cardDeck.drawCard();
        assertNotNull(drawnCard, "Card should be drawn.");
        assertEquals(2, drawnCard.getFaceValue(), "Second drawn card should have face value 2.");

        assertTrue(cardDeck.isEmpty(), "Deck should be empty after drawing all cards.");
    }

    // Test 12: Testing play concurrency
    @Test
    void testConcurrentAddAndDraw() throws InterruptedException {
        // Countdown latch to ensure both threads start at the same time
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1);

        Thread adder = new Thread(() -> {
            try {
                startSignal.await(); // Wait until both threads are ready to start
                for (int i = 1; i <= 50; i++) {
                    cardDeck.addCard(new Card(i));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneSignal.countDown(); // Signal completion of the adder thread
            }
        });

        Thread drawer = new Thread(() -> {
            try {
                startSignal.await(); // Wait until both threads are ready to start
                for (int i = 1; i <= 50; i++) {
                    cardDeck.drawCard();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneSignal.countDown(); // Signal completion of the drawer thread
            }
        });

        adder.start();
        drawer.start();

        // Signal both threads to start at the same time
        startSignal.countDown();
        doneSignal.await(); // Wait for both threads to finish

        assertTrue(cardDeck.isEmpty(), "Deck should be empty after concurrent add and draw operations.");
    }


    // Test 13: Testing if the deck remains as a string throughout play-through
    @Test
    void testOfferAndToString() {
        cardDeck.offer(new Card(10));
        cardDeck.offer(new Card(20));

        String deckString = cardDeck.toString();
        assertTrue(deckString.contains("Deck: [10, 20]"), "Deck string should represent the correct order of cards.");
    }

    // Test 14: Null card value test
    @Test
    void testOfferNullCard() {
        assertThrows(NullPointerException.class, () -> cardDeck.offer(null), "Offering a null card should throw NullPointerException.");
    }

    // Test 15: Adding a null card to a hand test
    @Test
    void testAddCardNullCard() {
        assertThrows(NullPointerException.class, () -> cardDeck.addCard(null), "Adding a null card should throw NullPointerException.");
    }

    // Test 16: What does drawing a null from the deck do?
    @Test
    void testDrawFromDeckWithNullCard() {
        assertThrows(NullPointerException.class, () -> {
            cardDeck.addCard(null);
            cardDeck.drawCard();
        }, "Adding and drawing a null card should throw NullPointerException.");
    }
}