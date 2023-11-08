package io.ids.argus.extension.modelchecking.conf;
import io.ids.argus.core.conf.propertie.ArgusProperties;

public class ModelCheckingProperties extends ArgusProperties {

    private static final String PROPERTIES_PATH = "argus-module.properties";

    private static final ModelCheckingProperties instance = new ModelCheckingProperties();

    private ModelCheckingProperties() {
    }

    public static ModelCheckingProperties get() {
        return instance;
    }

    @Override
    public String initPath() {
        return PROPERTIES_PATH;
    }

    public String getModelFilesPath() {
        return getString("model.files");
    }
    public String getScanResultPath() {
        return getString("scan.result");
    }
    public String getModelCheckPath() {
        return getString("model.check");
    }
}
