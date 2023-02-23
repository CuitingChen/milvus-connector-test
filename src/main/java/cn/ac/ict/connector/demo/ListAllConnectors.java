package cn.ac.ict.connector.demo;

import org.apache.flink.table.factories.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ListAllConnectors {

    private static final Logger log = LoggerFactory.getLogger(ListAllConnectors.class);

    public static void main(String[] args) {
        ServiceLoader<Factory> serviceLoader = ServiceLoader.load(Factory.class);
        Iterator<Factory> it = serviceLoader.iterator();
        while (it.hasNext()) {
            try {
                Factory tf = it.next();
                String id = tf.factoryIdentifier();
                System.out.println(id);
            } catch (Exception e) {
                log.error(e.toString());
            }

        }
    }

}
