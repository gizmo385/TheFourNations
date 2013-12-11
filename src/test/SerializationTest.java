package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import model.Civilization;
import model.CivilizationState;
import model.SaveLoadManager;
import model.map.Map;
import model.map.Resource;
import model.map.ResourceType;
import model.map.Terrain;
import model.structures.Bed;
import model.structures.Well;
import model.tasks.BuildStructureTask;

import org.junit.Test;

public class SerializationTest {

	@Test public void testSerialization() {
		//Get civ
		Civilization civ = Civilization.getInstance();
		
		//Create serialization object
		CivilizationState civStateInit = new CivilizationState( civ );

		//Add some stuff to the civilization
		Bed b = new Bed( 0, "BED", Resource.tree );
		civ.addStructure( b );
		civ.setResourceAmount( ResourceType.wood, 5000 );
		civ.setMap( new Map() );
		civ.getMap().getCell(300).setTerrain(Terrain.plains); //Prevent DisallowedTaskException
		civ.addTaskToQueue( new BuildStructureTask(5, 299, 300, civ.getMap(), new Well( 300, "WELL", Resource.stone ) ) );

		//Assertions
		assertEquals( 5000, civ.getResourceAmount( ResourceType.wood ) );
		assertTrue( civ.getStructures().contains(b) );
		assertFalse( civ.getTaskQueue().isEmpty() );

		//Parse serialization
		civ.parseCivilizationState(civStateInit);

		//Assertions
		assertFalse( 5000 == civ.getResourceAmount(ResourceType.wood) );
		assertFalse( civ.getStructures().contains(b) );
		assertTrue( civ.getTaskQueue().isEmpty() );
		
	}
	
	@Test public void saveSomeStates() {
		//Get civilization and save it
		Civilization civ = Civilization.getInstance();
		CivilizationState cs1 = new CivilizationState(civ);
		SaveLoadManager.saveGame( "cs1", cs1 );
		
		//Change some stuff
		civ.setResourceAmount( ResourceType.wood, 500 );
		civ.setResourceAmount( ResourceType.stone, 300 );
		
		//Save another persistant state
		CivilizationState cs2 = new CivilizationState(civ);
		SaveLoadManager.saveGame( "cs2", cs2 );
		
		//Get the saves located in the file tree
		System.out.print( "Existing saves: " );
		ArrayList<String> firstTwoSaves = SaveLoadManager.getSavedGames();
		System.out.println( firstTwoSaves );
		assertEquals( 2, firstTwoSaves.size() );
		System.out.println();

		
		//Load and test the first state
		System.out.println( "CS1" );
		System.out.println("------------------------------");
		CivilizationState cs1Loaded = SaveLoadManager.loadGame("cs1");
		System.out.println( "Structures:\t" + cs1Loaded.getStructures().size() );
		System.out.println( "Units to Kill:\t" + cs1Loaded.getUnitsToKill().size() );
		System.out.println( "Resource amounts:" );
		for( ResourceType rt : ResourceType.values() ) {
			System.out.println("\t" + rt + ":\t" + cs1.getGlobalResourcePool().get(rt) );
		}
		System.out.println( "Task Queue:\t" + cs1Loaded.getTaskQueue().size() );
		assertEquals( 0, cs1Loaded.getStructures().size() );
		assertEquals( 0, cs1Loaded.getUnitsToKill().size() );
		for( ResourceType rt : ResourceType.values() ) assertEquals( 0, cs1.getGlobalResourcePool().get(rt).intValue());
		assertEquals( 0, cs1Loaded.getTaskQueue().size() );
		System.out.println();
		
		//Load and test the second state
		System.out.println( "CS2" );
		System.out.println("------------------------------");
		CivilizationState cs2Loaded = SaveLoadManager.loadGame("cs2");
		System.out.println( "Structures:\t" + cs2Loaded.getStructures().size() );
		System.out.println( "Units to Kill:\t" + cs2Loaded.getUnitsToKill().size() );
		System.out.println( "Resource amounts:\t" );
		for( ResourceType rt : ResourceType.values() ) {
			System.out.println("\t" + rt + ":\t" + cs2.getGlobalResourcePool().get(rt) );
		}
		System.out.println( "Task Queue:\t" + cs2Loaded.getTaskQueue().size() );
		assertEquals( 0, cs2Loaded.getStructures().size() );
		for( ResourceType rt : ResourceType.values() ) assertEquals( 0, cs1.getGlobalResourcePool().get(rt).intValue());
		assertEquals( 0, cs2Loaded.getUnitsToKill().size() );
		assertEquals( 0, cs2Loaded.getTaskQueue().size() );
		System.out.println();
	}
}
