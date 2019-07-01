package org.cboard.dataprovider.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by zyong on 2017/4/24.
 */
@Data
public class CompositeConfig extends ConfigComponent {

    private String type;
    private ArrayList<ConfigComponent> configComponents = new ArrayList<>();


    @Override
    public Iterator<ConfigComponent> getIterator() {
        return configComponents.iterator();
    }
}
