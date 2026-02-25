package pro.azhidkov.mariotte.cases.platform.extensions

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import pro.azhidkov.mariotte.fixtures.object_mothers.resetRandom

class ResetRandomExtension : BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        resetRandom()
    }

}
