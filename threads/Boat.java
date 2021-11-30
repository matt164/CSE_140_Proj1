package nachos.threads;

import nachos.ag.BoatGrader;


public class Boat {
	static BoatGrader bg;
	static boolean not_done;
	static boolean boat_is_on_oahu;
	static Lock lock;
	static int children_on_boat;
	
	// your code here
	static int children_on_oahu;
	static int children_on_molokai;
	static int adults_on_oahu;
	static int adults_on_molokai;
	static int total_children;
	static int total_adults;

//NEW EDITS2
	static Lock boatLock;

	static Condition adultOahu;
	static Condition adultMolokai;
	static Condition childrenOahu;
	static Condition childrenMolokai;
	//END EDITS2

	public static void selfTest() {
		BoatGrader b = new BoatGrader();

	//	System.out.println("\n ***Testing Boats with only 2 children***");
	//	begin(0, 2, b);

	//	System.out.println("\n ***Testing Boats with only 2 children, 1 adult***");
	//	begin(1, 2, b);

	//	System.out.println("\n ***Testing Boats with 3 children, 3 adult***");
	//	begin(1, 3, b);

	} 


	public static void begin(int adults, int children, BoatGrader b) {
		// Store the externally generated autograder in a class
		// variable to be accessible by children.
		bg = b;

		// Instantiate global variables here
		not_done = true;
		boat_is_on_oahu = true;
		boatLock = new Lock();
		children_on_boat = 0;
		
		// your code here
		children_on_oahu = children;
		adults_on_oahu = adults;
		children_on_molokai = 0;
		adults_on_molokai = 0;
		
		boatLock = new Lock();

		adultOahu = new Condition(boatLock);
		adultMolokai = new Condition(boatLock);
		childrenOahu = new Condition(boatLock);
		childrenMolokai = new Condition(boatLock);
		


		// Define runnable object for child thread.
		Runnable r_child = new Runnable() {
			public void run() {
				ChildItinerary();
			}
		}; // r_child Runnable()

		// Define runnable object for adult thread.
		Runnable r_adult = new Runnable() {
			public void run() {
				AdultItinerary();
			}
		}; // r_adult Runnable()

		// Spawn all adult threads.
		for (int i = 0; i < adults; i++) {
			new KThread(r_adult).setName("Adult " + Integer.toString(i + 1)).fork();
		} // after this for loop, all adult threads are spawned and sleeping

		// Spawn all child threads.
		for (int i = 0; i < children; i++) {
			new KThread(r_child).setName("Child " + Integer.toString(i + 1)).fork();
		} // after this for loop, all child threads are spawned and start running

		// hold main thread while solutions calls are made to the BoatGrader
		while (not_done)
			KThread.yield();
		// while loop ends when last children and all adults are on Molokai

	} 


	static void AdultItinerary() {
		
		/*
		 * This is where you should put your solutions. Make calls to the BoatGrader to
		 * show that it is synchronized. For example: bg.AdultRowToMolokai(); indicates
		 * that an adult has rowed the boat across to Molokai
		 */
		
		boatLock.acquire();
		
		// adult threads can only operate with the lock atomically
		// while there are still adults not asleep on Molokai
		while (not_done) {
			//your code here
			if(boat_is_on_oahu && children_on_oahu < 2 && children_on_boat == 0){
				// row adult self to Molokai and wake one child up so it can bring the
				// boat back to Oahu for another adult or last children
				bg.AdultRowToMolokai();
				adults_on_oahu--;
				boat_is_on_oahu = false;
				adults_on_molokai++;
				//sleep and wake threads
				childrenMolokai.wakeAll(); 
				adultMolokai.sleep();
			}
			else{
				adultOahu.sleep(); 
			}

		} // after while, boat is on Oahu and children do not need it.
	
	} // while not done and adult still need to get to Molokai

	


	static void ChildItinerary() {
		// child threads can only operate with the lock atomically
		boatLock.acquire();

		// while there are still adults and children not on Molokai
		while (not_done) {

			if(boat_is_on_oahu){
				if(children_on_oahu > 1 && children_on_boat == 0){
					children_on_oahu--;
					children_on_boat++;
					bg.ChildRowToMolokai();
					children_on_molokai++;
					childrenOahu.wakeAll();
					childrenMolokai.sleep();
				}
				else if(children_on_boat == 1){
					children_on_oahu--;
					children_on_boat++;
					bg.ChildRideToMolokai();
					boat_is_on_oahu = false;
					children_on_boat = 0;
					childrenMolokai.wake();
					if(children_on_oahu + adults_on_oahu == 0){
						not_done = false;
					}
					childrenMolokai.sleep();
				}
				else{
					childrenOahu.sleep();
				}
				
			}
			else{
				children_on_molokai--;
				children_on_oahu++;
				bg.ChildRowToOahu();
				boat_is_on_oahu = true;
				adultOahu.wakeAll();
				childrenOahu.wakeAll();
				childrenOahu.sleep();
			}
		} 
	}
} 
