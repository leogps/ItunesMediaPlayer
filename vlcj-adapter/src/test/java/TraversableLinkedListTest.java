
import com.gps.itunes.media.player.vlcj.player.impl.TraversableLinkedList;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;


/**
 * Created by leogps on 10/17/15.
 */
public class TraversableLinkedListTest {

    private TraversableLinkedList<Integer> traversableLinkedList;
    private int firstElement = 1;
    private int lastElement = 10;

    @BeforeMethod
    private void setup() {
        traversableLinkedList = new TraversableLinkedList<Integer>();
        for(int i = firstElement - 1; i < lastElement; ){
            traversableLinkedList.add(++i);
        }
    }

    @Test
    public void test() {

        Assert.assertNotNull(traversableLinkedList);

        Assert.assertTrue(traversableLinkedList.getFirstElement() == firstElement);
        Assert.assertTrue(traversableLinkedList.getLastElement() == lastElement);

        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser =
                traversableLinkedList.getListTraverser();

        testNext(traversableLinkedListTraverser);
        testPrevious(traversableLinkedListTraverser);

        testNext(traversableLinkedListTraverser);
        testPrevious(traversableLinkedListTraverser);

        testNext(traversableLinkedListTraverser);
        testPrevious(traversableLinkedListTraverser);

        testNext(traversableLinkedListTraverser);
        testPrevious(traversableLinkedListTraverser);
    }

    @Test
    public void testZigZag() {
        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser =
                traversableLinkedList.getListTraverser();

        int counter = 0;
        while(traversableLinkedListTraverser.hasNext()) {
            if(++ counter > 1000) {
                break;
            }
            System.out.println("Printing Next: " + traversableLinkedListTraverser.next());
            if(counter % 3 == 0 && traversableLinkedListTraverser.hasPrevious()) {
                System.out.println("Printing previous: " + traversableLinkedListTraverser.previous());
            }
        }
    }

    private void testNext(TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser) {
        System.out.println("Traversing forward: ");
        int counter = firstElement - 1;
        while(traversableLinkedListTraverser.hasNext()) {
            Integer i = traversableLinkedListTraverser.next();
            Assert.assertTrue(++counter == i);
            System.out.println(i);
        }
    }

    private void testPrevious(TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser) {
        System.out.println("Traversing backwards: ");
        int counter = lastElement + 1;
        while(traversableLinkedListTraverser.hasPrevious()) {
            Integer i = traversableLinkedListTraverser.previous();
            Assert.assertTrue(--counter == i);
            System.out.println(i);
        }
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testNextException() {
        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser =
                traversableLinkedList.getListTraverser();
        while(traversableLinkedListTraverser.hasNext()) {
            traversableLinkedListTraverser.next();
        }
        traversableLinkedListTraverser.next();
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testPreviousException() {
        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser =
                traversableLinkedList.getListTraverser();
        while(traversableLinkedListTraverser.hasPrevious()) {
            traversableLinkedListTraverser.previous();
        }
        traversableLinkedListTraverser.previous();
    }

    @Test
    /**
     * Tests the {@link NowPlayingList#size()} in a non-multi-threaded use case.
     */
    public void testSize() {
        Assert.assertEquals(traversableLinkedList.size(), traversableLinkedList.countElements());
    }

    @Test
    public void testClear() {
        for(int i = 0; i < 10; i++) {
            doTestClear();
        }
    }

    @Test
    public void testIndexOf() {
        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser = traversableLinkedList.getListTraverser();
        System.out.println("Traversing forward: ");
        int index = 0;
        while(traversableLinkedListTraverser.hasNext()) {
            Integer i = traversableLinkedListTraverser.next();
            Assert.assertEquals(index, traversableLinkedList.indexOf(i));
            System.out.println(i);
            ++index;
        }
    }

    @Test
    public void testRemove() {
        int size = traversableLinkedList.size();
        boolean removed = traversableLinkedList.remove(6);
        Assert.assertTrue(removed);
        Assert.assertEquals(traversableLinkedList.size(), size - 1);
        size = traversableLinkedList.size();

        removed = traversableLinkedList.remove(1);
        Assert.assertTrue(removed);
        Assert.assertEquals(traversableLinkedList.size(), size - 1);
        size = traversableLinkedList.size();

        removed = traversableLinkedList.remove(4);
        Assert.assertTrue(removed);
        Assert.assertEquals(traversableLinkedList.size(), size - 1);
        size = traversableLinkedList.size();

        removed = traversableLinkedList.remove(1000);
        Assert.assertFalse(removed);
        Assert.assertEquals(traversableLinkedList.size(), size);

        removed = traversableLinkedList.remove(-1);
        Assert.assertFalse(removed);

        removed = traversableLinkedList.remove(10);
        Assert.assertTrue(removed);
        Assert.assertEquals(traversableLinkedList.size(), size - 1);
        size = traversableLinkedList.size();

        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser = traversableLinkedList.getListTraverser();
        System.out.println("Traversing forward: ");
        while(traversableLinkedListTraverser.hasNext()) {
            Integer i = traversableLinkedListTraverser.next();
            Assert.assertNotEquals(i, 1);
            Assert.assertNotEquals(i, 4);
            Assert.assertNotEquals(i, 6);
            Assert.assertNotEquals(i, 10);
            System.out.println(i);
        }
    }

    @Test(enabled = false) // Disabling this as this is very resource hungry test case.
    public void testMultiThreadedRemove() {
        for(int i = 0; i < 50; i++) {
            doTestMultiThreadedRemove();
        }
    }

    private void doTestMultiThreadedRemove() {
        final TraversableLinkedList<Integer> traversableLinkedList = new TraversableLinkedList<Integer>();

        for(int i = 0; i < 20; i++) {
            traversableLinkedList.add(i);
        }

        final Set<Integer> removedIntegers = new HashSet<Integer>();

        while(!traversableLinkedList.isEmpty()) {
            new Thread(new Runnable() {
                public void run() {
                    synchronized (removedIntegers) {
                        int value = Double.valueOf(Math.random() * 50).intValue();
                        if (!removedIntegers.contains(value)) {
                            System.out.println("Removing: " + value);
                            traversableLinkedList.remove(value);
                            removedIntegers.add(value);
                        }
                    }
                }
            }).start();

            new Thread(new Runnable() {
                public void run() {
                    while(traversableLinkedList.getListTraverser().hasNext()) {
                        synchronized (removedIntegers) {
                            try {
                                int value = traversableLinkedList.getListTraverser().next();
                                System.out.println("Found: " + value);
                                Assert.assertFalse(removedIntegers.contains(value));
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("This is expected: " + e);
                                Assert.assertFalse(traversableLinkedList.getListTraverser().hasNext());
                            }
                        }
                    }
                }
            }).start();
        }
    }

    private void doTestClear() {
        traversableLinkedList.clear();
        Assert.assertEquals(traversableLinkedList.getFirstElement(), null);
        Assert.assertEquals(traversableLinkedList.getLastElement(), null);
        Assert.assertEquals(traversableLinkedList.size(), 0);
        Assert.assertEquals(traversableLinkedList.countElements(), 0);
        Assert.assertEquals(traversableLinkedList.getListTraverser().hasNext(), false);
        Assert.assertEquals(traversableLinkedList.getListTraverser().hasPrevious(), false);
    }

    /**
     * A fixed number of threads will add elements simultaneously into the list keeping track of the number of elements added by each thread.
     * At the end it is checked to see that the list's size matches the number of elements added.
     * Also tests the {@link TraversableLinkedList#size()} in a multi-threaded use case.
     */
//    @Test
    public void testMultiThreadingAddition() throws InterruptedException {
        TraversableLinkedList<Integer> multiThreadableNowPlayingList = new TraversableLinkedList<Integer>();
        TraversableLinkedList<Integer>.ListTraverser<Integer> traversableLinkedListTraverser =
                multiThreadableNowPlayingList.getListTraverser();

        final boolean[] stopAddingElements = {false};
        System.out.println("Traversing forward: ");

        // Two threads adding elements simultaneously.
        int[] firstThreadElementAdditionCount = {0};
        int[] secondThreadElementAdditionCount = {0};
        int[] thirdThreadElementAdditionCount = {0};
        Thread t1 = getElementAddingThread(multiThreadableNowPlayingList, stopAddingElements, firstThreadElementAdditionCount);
        Thread t2 = getElementAddingThread(multiThreadableNowPlayingList, stopAddingElements, secondThreadElementAdditionCount);
        Thread t3 = getElementAddingThread(multiThreadableNowPlayingList, stopAddingElements, thirdThreadElementAdditionCount);

        t1.start();
        t2.start();
        t3.start();

        int counter = 0;
        while(traversableLinkedListTraverser.hasNext()) {
            Integer i = traversableLinkedListTraverser.next();
            System.out.println("Found element: "  + i);
            if(++counter > 1000) {
                // After some limited time, i.e., 1000 above,
                // ask the previous threads to stop adding elements into the list.
                // This makes it random and a this test a lil better.
                stopAddingElements[0] = true;
            }
        }

        // Wait for all element adding threads to complete.
        t1.join();
        t2.join();
        t3.join();


        System.out.println("First Thread added: " + firstThreadElementAdditionCount[0]);
        System.out.println("Second Thread added: " + secondThreadElementAdditionCount[0]);
        System.out.println("Third Thread added: " + thirdThreadElementAdditionCount[0]);
        System.out.println("Size: " + multiThreadableNowPlayingList.size());
        Assert.assertTrue(firstThreadElementAdditionCount[0] + secondThreadElementAdditionCount[0] + thirdThreadElementAdditionCount[0]
                == multiThreadableNowPlayingList.size());

        int iterativeCount = multiThreadableNowPlayingList.countElements();
        System.out.println("IterativeCount: " + iterativeCount);
        Assert.assertTrue(iterativeCount == multiThreadableNowPlayingList.size());

    }

    public static Thread getElementAddingThread(final TraversableLinkedList<Integer> multiThreadableNowPlayingList,
                                         final boolean[] stopAddingElements, final int[] elementAdditionCounter) {
        return new Thread(new Runnable() {
            public void run() {
                for(int i = 0; !stopAddingElements[0]; i++) {
                    multiThreadableNowPlayingList.add(i);
                    elementAdditionCounter[0] = elementAdditionCounter[0] + 1;
                }
            }
        });
    }
}
