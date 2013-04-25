package betsy.data.engines

class OpenEsb23Engine extends OpenEsbEngine {

    @Override
    String getName() {
        "openesb23"
    }

    String getXsltPath() {
        "src/main/xslt/${super.getName()}"
    }

}
