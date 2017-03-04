package configuration;

public class heroes {
	/*
	 * 1 Tick = 50 ms Cooldowns have a 0 tick
	 */
	// Shoop
	public static final double SHOOPMELEEDAMAGE = 2.0;		//Melee damage 2.0
	public static final double SHOOPBOLTSPEED = 2.5;		//Bolt travel speed 2.5
	public static final int SHOOPBOLTTICKS = 6 - 1;			//Bolt cooldown 0.3 seconds
	public static final double SHOOPBOLTDAMAGE = 1.5;		//Bolt damage 1.5
	public static final int SHOOPBOLTSMASHREDUCTION = 40;	//Bolt smash cooldown reduction 2.0 seconds
	public static final int SHOOPMAXCHARGES = 5;			//Secondary Max charges 5
	public static final int SHOOPCHARGEDRANGE = 60;			//Secondary range 60 blocks
	public static final double[] SHOOPCHARGEDDAMAGE = { 3.5, 5, 6.2, 6.7, 18 };	//Secondary damage 3.5,5.0,6.2,6.7,18.0
	public static final int SHOOPSMASHTICKS = 100 * 20 - 1; //Smash cooldown 100.0 seconds
	public static final int SHOOPSMASHDURATIONTICKS = 80;   //Smash duration 4.0 seconds
	public static final double SHOOPSMASHRANGE = 55.0;		//Smash beam range 55.0 blocks
	public static final double SHOOPSMASHDAMAGE = 10.0;		//Smash damage 10.0
	public static final double SHOOPPASSIVERANGE = 55.0;	//Passive travel range 55.0 blocks
	public static final double SHOOPPASSIVESPEED = 2.0;		//Passive travel speed 2.0
	public static final int SHOOPPASSIVEENERGY = 98;		//Passive energy cost 98
}
