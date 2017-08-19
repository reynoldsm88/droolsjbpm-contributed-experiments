package org.drools.examples.learner.refactored;

import java.util.ArrayList;
import java.util.List;

import org.drools.examples.learner.Car;
import org.drools.examples.learner.utils.CsvFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarExampleRefactored {

    private static final Logger LOG = LoggerFactory.getLogger( CarExampleRefactored.class );

    private static final boolean PRINT_DRL = true;

    public static void main( String... args ) throws Exception {

        String inputFile = System.getProperty( "user.dir" ) + "/src/main/resources/data/car/car.data.csv";
        List<Car> cars = new CsvFileReader<Car>().readObjects( inputFile, csv -> {
            Car car = new Car();
            car.setBuying( csv.get( "buying" ).trim() );
            car.setMaint( csv.get( "maint" ).trim() );
            car.setDoors( csv.get( "doors" ).trim() );
            car.setPersons( csv.get( "persons" ).trim() );
            car.setLug_boot( csv.get( "lug_boot".trim() ) );
            car.setSafety( csv.get( "safety" ).trim() );
            car.setTarget( csv.get( "target" ).trim() );
            return car;
        } );

        DroolsLearnerExample carExample = new DroolsLearnerExample( DroolsLearnerExample.SINGLE_C45E );
        carExample.runTraningExample( Car.class, new ArrayList<Object>( cars ) );

        if ( PRINT_DRL ) {
            System.out.println( carExample.getDrl() );
        }

        carExample.runGeneratedRules( new ArrayList<Object>( cars ) );
    }
}