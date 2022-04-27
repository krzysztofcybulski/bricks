package me.kcybulski.bricks.server.utils

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec

@ExperimentalKotest
abstract class BaseSpec(body: ShouldSpec.() -> Unit = {}) : ShouldSpec({

    isolationMode = IsolationMode.InstancePerTest
    testCoroutineDispatcher = true

    body(this)

})
