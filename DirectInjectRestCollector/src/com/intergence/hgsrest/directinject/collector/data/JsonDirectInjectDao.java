package com.intergence.hgsrest.directinject.collector.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intergence.hgsrest.directinject.collector.model.Model;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by stephen on 05/10/2015.
 */
public class JsonDirectInjectDao implements DirectInjectDao {

    private final Logger log = Logger.getLogger(this.getClass());

    private Model model;

    @Override
    public Model getModel() {
        checkNotNull(model);
        return model;
    }

    public void setModelClasspathFilename(String modelFilename) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(modelFilename);
        log.info("Loading Refinement rules from [" + modelFilename + "]");

        try {
            InputStreamReader reader = new InputStreamReader(resourceAsStream);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            model = gson.fromJson(reader, Model.class);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Error reading json model file [" + modelFilename + "]", e);
        }
    }
}
